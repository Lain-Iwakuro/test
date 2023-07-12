import chisel3._
import chisel3.util._
//import chisel3.experimental.ChiselEnum

class DataSource(width: Int, data: Array[Int]) extends Module {
  val io = IO(new Bundle {
    val in_rst = Input(Bool())
    val in_en = Input(Bool())
    val in_ready = Input(Bool())
    val out_bits = Output(UInt(width.W))
    val out_valid = Output(Bool())
  })

  val dataRegs = RegInit(
    VecInit((0 until data.length).map(data(_).U(width.W)))
  )
  val outBitsReg = RegInit(0.U(width.W))
  io.out_bits := outBitsReg
  val outValidReg = RegInit(false.B)
  io.out_valid := outValidReg
  val ptrReg = RegInit(
    data.length.U(log2Ceil(data.length + 1).W)
  ) // can represent length

  when(io.in_rst) {
    outBitsReg := dataRegs(0)
    outValidReg := true.B
    ptrReg := 1.U(log2Ceil(data.length + 1).W)
  }.otherwise {
    outValidReg := io.in_en && ptrReg < data.length.U
    when(io.in_en) {
      when(io.in_ready && ptrReg < data.length.U) {
        outBitsReg := dataRegs(ptrReg)
        ptrReg := ptrReg +& 1.U(log2Ceil(data.length + 1).W)
      }
      //printf(p"ptrReg = ${ptrReg}\n")
      //printf(p"dataRegs(ptrReg) = ${dataRegs(ptrReg)}\n")
      printf(p"out_bits = ${io.out_bits}, out_valid = ${io.out_valid}\n")
    }
  }
  
}
