package cl.exe.stream

import java.nio.file.{Paths, Path, Files}

import akka.actor._
import akka.camel.{CamelMessage, Consumer}
import akka.stream.{OverflowStrategy, ActorMaterializer}
import akka.stream.scaladsl._
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import org.apache.camel.scala.dsl.builder.RouteBuilder
import org.apache.camel.component.file.GenericFile
import org.apache.camel.impl.SimpleRegistry
import org.slf4j.LoggerFactory
import java.io.File

import scala.concurrent.Future




class ActorSFTP (actorSource : ActorRef) extends Actor with ActorLogging with Consumer {
  def endpointUri: String = s"file://${SampleApp.localWorkDirectory}?autoCreate=true&move=processed"

  def receive = {
    case m: CamelMessage => {
      log.info(m.toString())
      import scala.io.Source
      for(line <- m.bodyAs[String].split("\\s"))
        actorSource ! line


    }
  }


  override def preStart(): Unit = {
    super.preStart()
    camel.context.addRoutes(new RouteBuilder{
          from(s"${SampleApp.sftp}&binary=true&autoCreate=true&move=processed&localWorkDirectory=/tmp")-->(s"file://${SampleApp.localWorkDirectory}")
    })
  }
}


class ActorBytes extends Actor with ActorLogging {

  def receive = {
    case b : ByteString => {
      print(s"receive: ${b.utf8String}")
    }
    case m => println(m)
  }
}

object SampleApp extends App {
  val log = LoggerFactory.getLogger(this.getClass)
  log.info("SampleApp Application starting")
  val config = ConfigFactory.load
  val localWorkDirectory = Paths.get(config.getString("sample.localWorkDirectory"));
  val sftp = config.getString("sample.sftp")
  log.info(s"Working directory $localWorkDirectory")
  log.info(s"SFTP  $sftp")
  log.info(s"Initializing Actor System")
  implicit val system = ActorSystem("sampleSystem")
  import system.dispatcher
  implicit val materializer = ActorMaterializer()

  //SAMPLE1



  /*val text =
    """|Lorem Ipsum is simply dummy text of the printing and typesetting industry.
      |Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,
      |when an unknown printer took a galley of type and scrambled it to make a type
      |specimen book.""".stripMargin

  val src = Source(()  => text.split("\\s").iterator)
  val f = Flow[String].map(_.toUpperCase())
  val snk = Sink.foreach[String](println)
   src.via(f).to(snk).run()(materializer)
  */



 //SAMPLE 2


/*
   val text =
    """|Lorem Ipsum is simply dummy text of the printing and typesetting industry.
      |Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,
      |when an unknown printer took a galley of type and scrambled it to make a type
      |specimen book.""".stripMargin

  Source(() => text.split("\\s").iterator).
    map(_.toUpperCase).
    runForeach(println).
    onComplete(_ => println("*** Done Sample 2"))
*/

  import akka.stream.io.Implicits._

  //SAMPLES FILE

  val file : File = new File("archivo.txt")

  //SAMPLE 3

 /*
  val source  = Source.synchronousFile(file,5)
  val sink  = Sink.foreach[ByteString](b  => println(b.utf8String) )
  source.to(sink).run().onComplete(_ => println("##### Done Sample 3"))
*/

  //SAMPLE 4 Actor Sink


/*
  val source  = Source.synchronousFile(file,11)
  val actorByte = system.actorOf(Props[ActorBytes],name="actorBytes")
  val actorSink = Sink.actorRef[ByteString](actorByte, "Done Sample 4")
  source.runWith(actorSink)
  */

  // SAMPLE 5 Actor Source and Sink

/*  val actorByte = system.actorOf(Props[ActorBytes],name="actorBytes")
  val actorSink = Sink.actorRef[ByteString](actorByte, "Completed")
  val actorSource = Source.actorRef[ByteString](11,OverflowStrategy.dropTail).to(actorSink).run()
  (1 to 10).foreach(_ => actorSource ! ByteString("ABCDEFGHIJKLMNOPQRSTUVXYZ\n"))
*/


  //SAMPLE 6 Graph


/*
  val source  = Source.synchronousFile(file,11)
  val sink  = Sink.foreach[ByteString](b  => print(b.utf8String) )
  val actorByte = system.actorOf(Props[ActorBytes],name="actorBytes")
  val actorSink = Sink.actorRef[ByteString](actorByte, "Sample 6 Completed")
  val g = FlowGraph.closed() { implicit builder: FlowGraph.Builder[Unit] =>
    import FlowGraph.Implicits._
    val bcast = builder.add(Broadcast[ByteString](2))

    source ~> bcast ~> actorSink
    bcast ~> sink
  }

  g.run()

*/
  //SAMPLE 7 CAMEL

  /*
  val actorByte = system.actorOf(Props[ActorBytes],name="actorBytes")
  val actorSink = Sink.actorRef[ByteString](actorByte, "Completed")
  val actorSource = Source.actorRef[ByteString](11,OverflowStrategy.dropTail).to(actorSink).run()
  val actorSFTP = system.actorOf(Props(new ActorSFTP(actorSource)))

  */


}