import chisel3._
import chisel3.util._
import chiseltest._
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import scala.jdk.OptionShape
/*
class TestSampleModule extends AnyFlatSpec with ChiselScalatestTester {
  "Adder" should "work" in {
    test(new SampleModule).withAnnotations(Seq(simulator.VerilatorBackendAnnotation)) { c =>
      c.io.in.poke(1.U)
      c.io.out.expect(1.U)

    }
  }
}
*/
class MyTest extends Module {
  val io = IO(new Bundle {
    val in_rst = Input(Bool())
    val in_en = Input(Bool())
  })
  
  //val moduleA = Module(new DataSource(10, Array(9, 16, 17, 18, 73, 32, 5, 691, 7, 8, 64, 129, 128)))
  //val moduleB = Module(new HashTable(3, 10, 8))
  //val moduleC = Module(new OpSource(1, Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)))

  val moduleA = Module(new DataSource(8, Array(0x59, 0x19, 0x29, 0x39, 0x09, 0x39, 0x06, 0x16, 0x07, 0x16, 0x01)))
  val moduleB = Module(new HashTable(4, 8, 6))
  val moduleC = Module(new DataSource(1, Array(0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0)))

  moduleA.io.in_rst := io.in_rst
  moduleA.io.in_en := io.in_en
  moduleA.io.in_ready := moduleB.io.out_ready

  moduleB.io.in_rst := io.in_rst
  moduleB.io.in_op := moduleC.io.out_bits
  moduleB.io.in_bits := moduleA.io.out_bits  
  moduleB.io.in_valid := moduleA.io.out_valid //&& moduleC.io.out_valid

  moduleC.io.in_rst := io.in_rst
  moduleC.io.in_en := io.in_en
  moduleC.io.in_ready := moduleB.io.out_ready

}

class Test extends AnyFlatSpec with ChiselScalatestTester {
  "Top" should "work" in {
    ///*
    test(new MyTest).withAnnotations(Seq(simulator.VerilatorBackendAnnotation)) { c => 
      c.io.in_rst.poke(true.B)
      c.clock.step(2)

      c.io.in_rst.poke(false.B)
      c.io.in_en.poke(true.B)
      //c.clock.step(7)
      c.clock.step(27)

    }
    //*/
    /*
    test (new HashTable(3, 10))
      .withAnnotations(Seq(simulator.VerilatorBackendAnnotation)) { c => 
      c.io.in_rst.poke(true.B)
      c.clock.step(1)
      c.io.out_ready.expect(true.B)

      c.io.in_rst.poke(false.B)
      c.io.in_en.poke(false.B)
      c.clock.step(1)
      c.io.out_ready.expect(false.B)

      c.io.in_en.poke(true.B)
      c.io.in_bits.poke(9.U)
      c.io.in_valid.poke(true.B)
      c.clock.step(1)
      c.io.out_ready.expect(true.B)

      c.io.in_bits.poke(16.U)
      c.clock.step(1)
      c.io.out_ready.expect(true.B)
      
      c.io.in_bits.poke(17.U)
      c.clock.step(1)
      c.io.out_ready.expect(false.B)

      c.io.in_bits.poke(18.U)
      c.clock.step(1)
      c.io.out_ready.expect(true.B)

      c.io.in_bits.poke(73.U)
      c.clock.step(1)
      c.io.out_ready.expect(false.B)

      c.io.in_bits.poke(32.U)
      c.clock.step(1)
      c.io.out_ready.expect(false.B)

      c.clock.step(1)
      c.io.out_ready.expect(true.B)
      
      c.clock.step(1)
      c.io.out_ready.expect(false.B)

      c.io.in_bits.poke(5.U)
      c.clock.step(1)
      c.io.out_ready.expect(true.B)
      
      c.clock.step(1)
      c.io.out_ready.expect(true.B)
    }
    */
    /*
    test(new OpSource(16, Array(1, 0, 1, 0, 0, 1)))
      .withAnnotations(Seq(simulator.VerilatorBackendAnnotation)) { c =>
        c.io.in_rst.poke(true.B)
        c.io.in_en.poke(false.B)
        c.io.in_ready.poke(false.B)
        c.clock.step(1)
        c.io.out_bits.expect(1.U)
        c.io.out_valid.expect(true.B)

        c.io.in_rst.poke(false.B)
        c.io.in_en.poke(true.B)
        c.clock.step(1)
        c.io.out_bits.expect(1.U)
        c.io.out_valid.expect(true.B)

        c.io.in_en.poke(false.B)
        c.clock.step(1)
        c.io.out_valid.expect(false.B)

        c.io.in_en.poke(true.B)
        c.clock.step(1)
        c.io.out_bits.expect(1.U)
        c.io.out_valid.expect(true.B)

        c.io.in_ready.poke(true.B)
        c.clock.step(1)
        c.io.out_bits.expect(0.U)
        c.io.out_valid.expect(true.B)

        c.io.in_en.poke(false.B)
        c.clock.step(1)
        c.io.out_valid.expect(false.B)

        c.io.in_en.poke(true.B)
        c.clock.step(1)
        c.io.out_bits.expect(1.U)
        c.io.out_valid.expect(true.B)

        c.io.in_ready.poke(false.B)
        c.clock.step(1)
        c.io.out_bits.expect(1.U)
        c.io.out_valid.expect(true.B)

        c.clock.step(1)
        c.io.out_bits.expect(1.U)
        c.io.out_valid.expect(true.B)

        c.io.in_ready.poke(true.B)
        c.clock.step(1)
        c.io.out_bits.expect(0.U)
        c.io.out_valid.expect(true.B)

        c.clock.step(1)
        c.io.out_bits.expect(0.U)
        c.io.out_valid.expect(true.B)
        
        c.clock.step(1)
        c.io.out_bits.expect(1.U)
        c.io.out_valid.expect(true.B)
        
        c.clock.step(1)
        c.io.out_valid.expect(false.B)
      }
      */
    /*
    test(new DataSource(16, Array(100,2,37,42,5)))
      .withAnnotations(Seq(simulator.VerilatorBackendAnnotation)) { c =>
        c.io.in_rst.poke(true.B)
        c.io.in_en.poke(false.B)
        c.io.in_ready.poke(false.B)
        c.clock.step(1)
        c.io.out_bits.expect(100.U)
        c.io.out_valid.expect(true.B)

        c.io.in_rst.poke(false.B)
        c.io.in_en.poke(true.B)
        c.clock.step(1)
        c.io.out_bits.expect(100.U)
        c.io.out_valid.expect(true.B)

        c.io.in_en.poke(false.B)
        c.clock.step(1)
        c.io.out_valid.expect(false.B)

        c.io.in_en.poke(true.B)
        c.clock.step(1)
        c.io.out_bits.expect(100.U)
        c.io.out_valid.expect(true.B)

        c.io.in_ready.poke(true.B)
        c.clock.step(1)
        c.io.out_bits.expect(2.U)
        c.io.out_valid.expect(true.B)

        c.io.in_en.poke(false.B)
        c.clock.step(1)
        c.io.out_valid.expect(false.B)

        c.io.in_en.poke(true.B)
        c.clock.step(1)
        c.io.out_bits.expect(37.U)
        c.io.out_valid.expect(true.B)

        c.io.in_ready.poke(false.B)
        c.clock.step(1)
        c.io.out_bits.expect(37.U)
        c.io.out_valid.expect(true.B)

        c.clock.step(1)
        c.io.out_bits.expect(37.U)
        c.io.out_valid.expect(true.B)

        c.io.in_ready.poke(true.B)
        c.clock.step(1)
        c.io.out_bits.expect(42.U)
        c.io.out_valid.expect(true.B)

        c.clock.step(1)
        c.io.out_bits.expect(5.U)
        c.io.out_valid.expect(true.B)
        
        c.clock.step(1)
        //c.io.out_bits.expect(5.U)
        c.io.out_valid.expect(false.B)
        
      }
    */
  }
}
