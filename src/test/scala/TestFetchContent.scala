import org.scalatest.FlatSpec


class TestFetchContent extends FlatSpec {

  "this document " should "be fetchable " in {
    import db.ctx._
    assert(  dbAccessWrapper.hasPdf("http://opac.nebis.ch/objects/pdf/e65_3-7170-0201-5_01.pdf"))

  }

  "this document " should "not be fetchable " in {
    import db.ctx._
    assert(  ! dbAccessWrapper.hasPdf("http://opac.nebis.ch/objects/pdf/e65_3-7170-0201-5_01.pd"))

  }


}
