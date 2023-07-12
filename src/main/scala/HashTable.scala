import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum
import cats.conversions.all
//import chiseltest._
//import org.scalatest._

object State extends ChiselEnum {
  val add = Value(0.U)
  val search = Value(1.U)
}

class HashTable(logSize: Int, width: Int) extends Module {
  val io = IO(new Bundle {
    val in_rst = Input(Bool())
    val in_en = Input(Bool())
    val in_bits = Input(UInt(width.W))
    val in_valid = Input(Bool())
    val out_ready = Output(Bool())
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
  val state = RegInit(State.add)
  val outReadyReg = RegInit(false.B)
  io.out_ready := outReadyReg
  // io.out_ready := state === State.add

  when(io.in_rst) {
    for (i <- 0 until (math.pow(2, logSize + 1).toInt)) {
      dataRegs(i) := 0.U
      nextRegs(i) := 0.U
    }
    allocPtrReg := math.pow(2, logSize).toInt.U
    nextPtrReg := 0.U
    bitsReg := 0.U
    state := State.add
    outReadyReg := true.B
  }.otherwise {
    when(io.in_en) {
      when(state === State.add) {
        when(allocPtrReg(logSize) === 1.U) {
          when(io.in_valid) {
            when(~tagRegs(io.in_bits(logSize - 1, 0))) {
              dataRegs(io.in_bits(logSize - 1, 0)) := io.in_bits
              printf(p"table writes ${io.in_bits} into addr ${io.in_bits(logSize - 1, 0)}\n")
              tagRegs(io.in_bits(logSize - 1, 0)) := true.B
              // state := State.add
              outReadyReg := true.B
            }.otherwise {
              state := State.search
              bitsReg := io.in_bits
              nextPtrReg := io.in_bits(logSize - 1, 0)
              printf(p"table address ${io.in_bits(logSize - 1, 0)} is occupied!\n")
              outReadyReg := false.B
            }
          }.otherwise {
            outReadyReg := true.B
          }
        }.otherwise {
            outReadyReg := false.B
        }
      }.elsewhen(state === State.search) {
        when(nextRegs(nextPtrReg) === 0.U) {
          dataRegs(allocPtrReg) := bitsReg
          printf(p"table writes ${bitsReg} into addr ${allocPtrReg}\n")
          tagRegs(allocPtrReg) := true.B
          nextRegs(nextPtrReg) := allocPtrReg
          allocPtrReg := allocPtrReg + 1.U
          state := State.add
          outReadyReg := true.B
        }.otherwise {
          nextPtrReg := nextRegs(nextPtrReg)
          printf(p"table address ${nextRegs(nextPtrReg)} is occupied!\n")
        }
      }
    }.otherwise {
      state := State.add
      outReadyReg := false.B
    }
  }

}
