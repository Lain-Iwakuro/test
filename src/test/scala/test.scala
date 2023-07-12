import chisel3._
import chisel3.util._
import chiseltest._
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
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
  
  val moduleA = Module(new DataSource(10, Array(9, 16, 17, 18, 73, 32, 5, 691, 7, 8, 64, 129, 128)))
  val moduleB = Module(new HashTable(3, 10))

  moduleA.io.in_rst := io.in_rst
  moduleA.io.in_en := io.in_en
  moduleA.io.in_ready := moduleB.io.out_ready

  moduleB.io.in_rst := io.in_rst
  moduleB.io.in_en := io.in_en
  moduleB.io.in_bits := moduleA.io.out_bits  
  moduleB.io.in_valid := moduleA.io.out_valid  

  //chisel3.assert(moduleB.io.out === 1.U) // 添加断言
}

class Test extends AnyFlatSpec with ChiselScalatestTester {
  "Top" should "work" in {
    test(new MyTest).withAnnotations(Seq(simulator.VerilatorBackendAnnotation)) { c => 
      c.io.in_rst.poke(true.B)
      c.clock.step(1)

      c.io.in_rst.poke(false.B)
      c.io.in_en.poke(true.B)
      c.clock.step(40)

    }
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
