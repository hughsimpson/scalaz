package scalaz

trait FoldLeft[-F[_]] {
  def foldLeft[B, A](t: F[A], b: B, f: (B, A) => B): B
}

object FoldLeft {
  implicit val IdentityFoldLeft = new FoldLeft[Identity] {
    def foldLeft[B, A](t: Identity[A], b: B, f: (B, A) => B) = f(b, t.value)
  }

  implicit val NonEmptyListFoldLeft = new FoldLeft[NonEmptyList] {
    def foldLeft[B, A](t: NonEmptyList[A], b: B, f: (B, A) => B) = t.list.foldLeft(b)(f)
  }

  implicit val StateFoldLeft = new FoldLeft[PartialApply1Of2[State, Unit]#Apply] {
    def foldLeft[B, A](t: State[Unit, A], b: B, f: (B, A) => B) = f(b, t(())._2)
  }

  implicit val Tuple1FoldLeft = new FoldLeft[Tuple1] {
    def foldLeft[B, A](t: Tuple1[A], b: B, f: (B, A) => B) = f(b, t._1)
  }

  implicit val Function0FoldLeft = new FoldLeft[Function0] {
    def foldLeft[B, A](t: Function0[A], b: B, f: (B, A) => B) = f(b, t.apply)
  }

  implicit val OptionFoldLeft = new FoldLeft[Option] {
    def foldLeft[B, A](t: Option[A], b: B, f: (B, A) => B) = t match {
      case Some(a) => f(b, a)
      case None => b
    }
  }

  import S._
  import Function2W._
  import FoldRight._
  implicit val ZipperFoldLeft: FoldLeft[Zipper] = new FoldLeft[Zipper] {
    def foldLeft[B, A](t: Zipper[A], b: B, f: (B, A) => B): B =
      t.lefts.foldRight(Stream.cons(t.focus, t.rights).foldLeft(b)(f))(f.flip)
  }

  implicit val ArrayFoldLeft = new FoldLeft[Array] {
    def foldLeft[B, A](t: Array[A], b: B, f: (B, A) => B) = t.foldLeft(b)(f)
  }

  implicit def EitherLeftFoldLeft[X] = new FoldLeft[PartialApply1Of2[Either.LeftProjection, X]#Flip] {
    def foldLeft[B, A](e: Either.LeftProjection[A, X], b: B, f: (B, A) => B) = OptionFoldLeft.foldLeft(e.toOption, b ,f)
  }

  implicit def EitherRightFoldLeft[X] = new FoldLeft[PartialApply1Of2[Either.RightProjection, X]#Apply] {
    def foldLeft[B, A](e: Either.RightProjection[X, A], b: B, f: (B, A) => B) = OptionFoldLeft.foldLeft(e.toOption, b ,f)
  }

  implicit val IterableFoldLeft = new FoldLeft[Iterable] {
    def foldLeft[B, A](t: Iterable[A], b: B, f: (B, A) => B) = t.foldLeft(b)(f)
  }

  implicit val JavaIterableFoldLeft = new FoldLeft[java.lang.Iterable] {
    def foldLeft[B, A](t: java.lang.Iterable[A], b: B, f: (B, A) => B) = {
      var x = b
      val i = t.iterator

      while(i.hasNext) {
        val n = i.next
        x = f(x, n)
      }

      x
    }
  }
}