package com.katlex.ccs

import java.io.{FileFilter, File}
import util.matching.Regex

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
      case Directory(dir) => f.listFiles().toSet[File].flatMap(_ ** filter)
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

