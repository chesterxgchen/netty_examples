/**
 *
 * Date: 11/7/14
 * Time: 9:34 PM
 * @author chester
 */

import sbt._

object Dependencies {

  val resolutionRepos = Seq(
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
  )

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  val nettyAll       = "io.netty"                                % "netty-all"                     % "4.0.4.Final"

}


