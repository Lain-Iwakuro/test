import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum
//import chiseltest._
//import org.scalatest._
object Op extends ChiselEnum {
  val get = Value(0.U)
  val add = Value(1.U)
  val rem = Value(2.U)
}

class HashTable(logRegs: Int, regWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in_key = Input(UInt(logRegs.W))
    val in_value = Input(UInt(regWidth.W))
    val in_op = Input(Op()) // 0 for get, 1 for add, 2 for remove
    val out_value = Output(UInt(regWidth.W))
    val out_valid = Output(UInt(1.W))
  })

  val regs = RegInit(
    VecInit(Seq.fill(math.pow(2, logRegs).toInt)(0.U(regWidth.W)))
  )
  val tags = RegInit(VecInit(Seq.fill(math.pow(2, logRegs).toInt)(0.U(1.W))))
  val reg_valid = RegInit(0.U(1.W))
  io.out_valid := reg_valid
  val reg_value = RegInit(0.U(regWidth.W))
  io.out_value := reg_value

  when(io.in_op === Op.get) { // get
    reg_valid := tags(io.in_key)
    reg_value := Mux(tags(io.in_key) === 1.U, regs(io.in_key), 0.U)
  }.elsewhen(io.in_op === Op.add) { // add
    reg_valid := ~tags(io.in_key)
    reg_value := 0.U
    when(tags(io.in_key) === 0.U) {
      regs(io.in_key) := io.in_value
      tags(io.in_key) := 1.U
    }
  }.elsewhen(io.in_op === Op.rem) { // remove
    reg_valid := tags(io.in_key)
    reg_value := 0.U
    tags(io.in_key) := 0.U
  }.otherwise {
    reg_value := 0.U
    reg_valid := 0.U
  }
}

object Main extends App {}
