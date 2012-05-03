package cinema.crew

import akka.kernel.Bootable
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import java.net.InetAddress


class GraphGrip extends Bootable {
  // Akka was giving me problems with the default .getHostAddress()
  // For some reason, .getHostName() works, so that's why I'm doing this.
  val myHostName = InetAddress.getLocalHost().getHostName()
  val myConfig = """
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }
      remote {
        transport = "akka.remote.netty.NettyRemoteTransport"
        netty {
          hostname = """ + "\"" + myHostName + "\"" + """
          port = 2552
          message-frame-size = 500 MiB
        }
      }
    }
    """
  println("Deployed on: " + myHostName)
  val customConf = ConfigFactory.parseString(myConfig)
    
  val system = ActorSystem("GraphGrip", ConfigFactory.load(customConf))

  def startup() {
  }

  def shutdown() {
    system.shutdown()
  }
}

object GraphGrip {
  def main(args: Array[String]) {
    new GraphGrip
    println("Started GraphGrip - waiting for messages...")
  }
}