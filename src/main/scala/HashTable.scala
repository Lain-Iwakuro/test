import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum
//import cats.conversions.all
//import chiseltest._
//import org.scalatest._

object State extends ChiselEnum {
  val idle = Value(0.U)
  // val add = Value(0.U)
  val addSearch = Value(1.U)
  // val get = Value(2.U)
  val getSearch = Value(3.U)
}

class HashTable(logSize: Int, width: Int, keyWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in_rst = Input(Bool())
    // val in_en = Input(Bool())
    val in_op = Input(UInt(1.W))
    // val in_ovalid = Input(Bool())
    val in_bits = Input(UInt(width.W))
    val in_valid = Input(Bool())
    val out_ready = Output(Bool())
    val out_found = Output(Bool())
    val out_bits = Output(UInt(width.W))
    val out_valid = Output(Bool())
  })
  val dataRegs = RegInit(
    VecInit(Seq.fill((math.pow(2, logSize + 1).toInt))(0.U(width.W)))
  )
  val tagRegs = RegInit(
    VecInit(Seq.fill((math.pow(2, logSize + 1).toInt))(false.B))
  )
  val nextRegs = RegInit(
    VecInit(
      Seq.fill((math.pow(2, logSize + 1).toInt))(0.U((logSize + 1).W))
    )
  )
  val allocPtrReg = RegInit(math.pow(2, logSize).toInt.U((logSize + 1).W))
  val nextPtrReg = RegInit(0.U((logSize + 1).W))
  val bitsReg = RegInit(0.U(width.W))
  val state = RegInit(State.idle)
  val outReadyReg = RegInit(false.B)
  io.out_ready := outReadyReg
  val outBitsReg = RegInit(0.U(width.W))
  io.out_bits := outBitsReg
  val outValidReg = RegInit(false.B)
  io.out_valid := outValidReg
  val outFoundReg = RegInit(false.B)
  io.out_found := outFoundReg
  // io.out_ready := state === State.add

  when(io.in_rst) {
    for (i <- 0 until (math.pow(2, logSize + 1).toInt)) {
      dataRegs(i) := 0.U
      nextRegs(i) := 0.U
    }
    allocPtrReg := math.pow(2, logSize).toInt.U
    nextPtrReg := 0.U
    bitsReg := 0.U
    state := State.idle
    outReadyReg := true.B
    outBitsReg := 0.U
    outValidReg := false.B
    outFoundReg := false.B
  }.otherwise {
    printf(p"table out_ready = ${outReadyReg}\n")
    when(state === State.idle) {
      when(io.in_valid) {
        val addr = io.in_bits(logSize - 1, 0)
        when(io.in_op === 0.U) { // add
          when(allocPtrReg(logSize) === 1.U) {
            when(~tagRegs(addr)) {
              dataRegs(addr) := io.in_bits
              printf(p"table writes ${io.in_bits} into addr ${addr}\n")
              tagRegs(addr) := true.B
              outReadyReg := true.B
            }.otherwise {
              state := State.addSearch
              bitsReg := io.in_bits
              nextPtrReg := addr
              printf(p"table address ${addr} is occupied!\n")
              outReadyReg := false.B
            }
          }.otherwise {
            outReadyReg := false.B
          }
        }.elsewhen(io.in_op === 1.U) { // get
          when(tagRegs(addr)) {
            when(
              dataRegs(addr)(keyWidth - 1, 0) === io.in_bits(keyWidth - 1, 0)
            ) {
              outFoundReg := true.B
              outBitsReg := dataRegs(addr)
              outValidReg := true.B
              printf(p"table value ${dataRegs(addr)} found!\n")
            }.elsewhen(nextRegs(addr) =/= 0.U) {
              state := State.getSearch
              bitsReg := io.in_bits
              nextPtrReg := nextRegs(addr)
              printf(p"table address ${addr} not found!\n")
              outReadyReg := false.B
            }.otherwise {
              outFoundReg := false.B
              outValidReg := true.B
              printf(p"table key ${io.in_bits(keyWidth - 1, 0)} not found!\n")
            }
          }.otherwise {
            outFoundReg := false.B
            outValidReg := true.B
          }
        }
      }.otherwise {
        outReadyReg := true.B
      }
    }.elsewhen(state === State.addSearch) {
      when(nextRegs(nextPtrReg) === 0.U) {
        dataRegs(allocPtrReg) := bitsReg
        printf(p"table writes ${bitsReg} into addr ${allocPtrReg}\n")
        tagRegs(allocPtrReg) := true.B
        nextRegs(nextPtrReg) := allocPtrReg
        allocPtrReg := allocPtrReg + 1.U
        state := State.idle
        outReadyReg := true.B
      }.otherwise {
        nextPtrReg := nextRegs(nextPtrReg)
        printf(p"table address ${nextRegs(nextPtrReg)} is occupied!\n")
      }
    }.elsewhen(state === State.getSearch) {
      when(dataRegs(nextPtrReg)(keyWidth - 1, 0) === bitsReg(keyWidth - 1, 0)) {
        outFoundReg := true.B
        outBitsReg := dataRegs(nextPtrReg)
        outValidReg := true.B
        printf(p"table value ${dataRegs(nextPtrReg)} found!\n")
        state := State.idle
        outReadyReg := true.B
      }.elsewhen(nextRegs(nextPtrReg) =/= 0.U) {
        nextPtrReg := nextRegs(nextPtrReg)
        printf(p"table address ${nextPtrReg} not found!\n")
      }.otherwise {
        outFoundReg := false.B
        outValidReg := true.B
        printf(p"table key ${bitsReg(keyWidth - 1, 0)} not found!\n")
        state := State.idle
        outReadyReg := true.B
      }
    }
    for(i <- (0 until (math.pow(2, logSize + 1).toInt))) {
        printf(p"${dataRegs(i)} ")
    }
    printf("\n")
    printf("------\n")
  }

}
