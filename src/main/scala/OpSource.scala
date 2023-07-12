import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

object op extends ChiselEnum {
  val add = Value(0.U)
  val get = Value(1.U)
}

class OpSource(width: Int = 1, op: Array[Int]) extends Module {
  val io = IO(new Bundle {
    val in_rst = Input(Bool())
    val in_en = Input(Bool())
    val in_ready = Input(Bool())
    val out_bits = Output(UInt(width.W))
    val out_valid = Output(Bool())
  })

  val opRegs = RegInit(
    VecInit((0 until op.length).map(op(_).U))
  )
  val outBitsReg = RegInit(0.U(width.W))
  io.out_bits := outBitsReg
  val outValidReg = RegInit(false.B)
  io.out_valid := outValidReg
  val ptrReg = RegInit(
    op.length.U(log2Ceil(op.length + 1).W)
  ) // can represent length

  when(io.in_rst) {
    outBitsReg := opRegs(0)
    outValidReg := true.B
    ptrReg := 1.U(log2Ceil(op.length + 1).W)
  }.otherwise {
    outValidReg := io.in_en && (ptrReg < op.length.U || ~io.in_ready)
    when(io.in_en) {
      when(io.in_ready && ptrReg < op.length.U) {
        outBitsReg := opRegs(ptrReg)
        ptrReg := ptrReg +& 1.U(log2Ceil(op.length + 1).W)
      }
      //printf(p"ptrReg = ${ptrReg}\n")
      //printf(p"opRegs(ptrReg) = ${opRegs(ptrReg)}\n")
      printf(p"op out_bits = ${io.out_bits}, out_valid = ${io.out_valid}\n")
    }
  }
  
}