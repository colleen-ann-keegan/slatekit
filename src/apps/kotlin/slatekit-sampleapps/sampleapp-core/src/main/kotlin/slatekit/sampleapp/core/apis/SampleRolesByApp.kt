package slatekit.sampleapp.core.apis

import slatekit.apis.Api
import slatekit.apis.ApiAction



@Api(area = "samples", name = "rolesapp", desc = "sample to test security", roles= "admin", auth = "app-roles", verb = "*", protocol = "*")
class SampleRolesByApp {

    @ApiAction(desc = "no roles allows access by anyone", roles= "")
    fun rolesNone(code:Int, tag:String): String {
        return "rolesNone $code $tag"
    }


    @ApiAction(desc = "* roles allows access by any authenticated in user", roles= "*")
    fun rolesAny(code:Int, tag:String): String {
        return "rolesAny $code $tag"
    }


    @ApiAction(desc = "allows access by specific role", roles= "dev")
    fun rolesSpecific(code:Int, tag:String): String  {
        return "rolesSpecific $code $tag"
    }


    @ApiAction(desc = "@parent refers to its parent role", roles= "@parent")
    fun rolesParent(code:Int, tag:String): String {
        return "rolesParent $code $tag"
    }
}
