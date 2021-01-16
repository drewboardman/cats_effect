package copyfile

import cats.effect.{IO, IOApp, Resource}

import java.io.{File, FileInputStream, FileOutputStream, InputStream, OutputStream}

object CopyFile extends IOApp {
  type FileSize = Long

  def copy(origin: File, destination: File): IO[FileSize] =
    inputOutputStreams(origin, destination).use {
      case (in, out) => transfer(in, out)
    }

  def transfer(origin: InputStream, destination: OutputStream): IO[FileSize] = ???

  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
      IO(new FileInputStream(f)) // make
      } { inStream =>
        IO(inStream.close()) // release
          .handleErrorWith(_ => IO.unit)
      }

  def outputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.make {
      IO(new FileOutputStream(f))
      } { outStream =>
        IO(outStream.close())
          .handleErrorWith(_ => IO.unit)
      }

  def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)
}
