import org.scalatest.FlatSpec


class TestFetchContent extends FlatSpec {

  //in statt ignore zum Aktivieren
  "this document " should "be fetchable " ignore  {
    import db.ctx._
    assert(  dbAccessWrapper.hasPdf("http://opac.nebis.ch/objects/pdf/e65_3-7170-0201-5_01.pdf"))

  }

  "this document " should "not be fetchable " ignore {
    import db.ctx._
    assert(  ! dbAccessWrapper.hasPdf("http://opac.nebis.ch/objects/pdf/e65_3-7170-0201-5_01.pd"))

  }


}
