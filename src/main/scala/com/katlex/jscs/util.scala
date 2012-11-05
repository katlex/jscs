package com.katlex.jscs

import java.io.{FileFilter, File}
import net.liftweb.common.{Full, Box}
import util.matching.Regex
import collection.GenTraversableOnce

trait FileUtil {
  object Directory {
    def unapply(f:File) = if (f.isDirectory && f.canRead) Some(f) else None
  }
  object ReadableFile {
    def unapply(f:File) = if (f.isFile && f.canRead) Some(f) else None
  }

  sealed case class FileOps(f:File) {
    def /(child:String) = new File(f.getAbsolutePath + File.separator + child)
    def **(filter:FileFilter):Set[File] = f match {
      case Directory(dir) => dir.listFiles().toSet[File].flatMap(_ ** filter)
      case ReadableFile(f) if (filter.accept(f)) => Set(f)
      case _ => Set()
    }
    def **():Set[File] = **(new FileFilter {
      def accept(pathname: File) = true
    })
    def **(filter:Regex):Set[File] = **(new FileFilter {
      def accept(pathname: File) = filter.findFirstIn(pathname.getAbsolutePath).isDefined
    })
  }
  implicit def toFileOps(f:File):FileOps = FileOps(f)

  lazy val home = sys.props.get("user.home").map(new File(_))
}

trait BoxUtil {
  sealed class BoxFoldOps[T](stream:Stream[T]) {
    def foldLeftBoxed[U](v:U)(f:(Box[U], T) => Box[U]):Box[U] =
      stream match {
        case head #:: tail => f(Full(v), head).flatMap {
          v1 => tail.foldLeftBoxed(v1)(f)
        }
        case _ => Full(v)
      }
    def /~:[U](v:U)(f:(Box[U], T) => Box[U]) = foldLeftBoxed(v)(f)
  }
  implicit def toBoxFoldOps[T](traversable:GenTraversableOnce[T]) = new BoxFoldOps(traversable.toStream)
}