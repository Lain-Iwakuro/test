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
class TestHashTable extends AnyFlatSpec with ChiselScalatestTester {
  "HashTable" should "work" in {
    test(new HashTable(5, 32))
      .withAnnotations(Seq(simulator.VerilatorBackendAnnotation)) { c =>
        // check initialize
        c.io.out_value.expect(0.U)
        c.io.out_valid.expect(0.U)

        // add
        c.io.in_op.poke(Op.add)
        c.io.in_key.poke(20.U)
        c.io.in_value.poke(5.U)
        c.clock.step(1)
        c.io.out_valid.expect(1.U)
        c.io.out_value.expect(0.U)
        
        c.clock.step(1) // add twice
        c.io.out_valid.expect(0.U)
        c.io.out_value.expect(0.U)

        c.io.in_key.poke(20.U) // add different value
        c.io.in_value.poke(6.U)
        c.clock.step(1)
        c.io.out_valid.expect(0.U)

        // get
        c.io.in_op.poke(Op.get)
        c.io.in_key.poke(20.U)
        c.clock.step(1)
        c.io.out_value.expect(5.U)
        c.io.out_valid.expect(1.U)

        c.io.in_key.poke(20.U)
        c.clock.step(1)
        c.io.out_value.expect(5.U)
        c.io.out_valid.expect(1.U)

        c.io.in_key.poke(1.U) // get not stored value
        c.io.out_value.expect(5.U)
        c.clock.step(1)
        c.io.out_value.expect(0.U)
        c.io.out_valid.expect(0.U)

        // remove
        c.io.in_op.poke(Op.rem)
        c.io.in_key.poke(7.U) // remove not stored value
        c.clock.step(1)
        c.io.out_valid.expect(0.U)

        c.io.in_op.poke(Op.get)
        c.io.in_key.poke(20.U)
        c.clock.step(1)
        c.io.out_valid.expect(1.U)

        c.io.in_op.poke(Op.rem)
        c.io.in_key.poke(20.U)
        c.clock.step(1)
        c.io.out_valid.expect(1.U)

        c.io.in_op.poke(Op.get)
        c.clock.step(1)
        c.io.out_valid.expect(0.U) // removed
      }
  }
}
