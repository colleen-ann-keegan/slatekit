/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common

object Funcs {

  def defaultOrExecute[T](condition:Boolean, defaultValue:T, f: => T): T = {
    if(condition){
      return defaultValue
    }
    f
  }


  def execute[T](data:Option[T], f:(T) => Unit): Unit = {
    data.fold(Unit)( item => {
      f(item)
      Unit
    })
  }


  def executeIf[T](condition:Boolean, data:Option[T], f:(T) => Unit): Unit = {
    if(condition) {
      data.fold(Unit)(item => {
        f(item)
        Unit
      })
    }
  }
}