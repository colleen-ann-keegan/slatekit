/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common


import java.io.File


object Files {

    /**
     * builds a folder name based on the date.
     * e.g. YYYYMMDD - 201705030
     */
    fun folderNameByDate(): String = DateTime.now().toStringNumeric()


    /**
     * creates a directory in the users directory
     */
    fun mkUserDir(dir: String): String {
        val userHome = System.getProperty("user.home")
        return mkDir(userHome, dir)
    }


    /**
     * creates the directory using the parent directory and child directory
     * checks if the directory already exists
     */
    fun mkDir(parent: String, child: String): String {
        val dir = File(parent, child)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir.absolutePath
    }


    /**
     * reads lines in the file path supplied
     */
    fun readLines(path: String): List<String> = File(path).readLines()


    /**
     * writes a file to a date based directory inside an app directory inside the users home directory
     *
     * e.g. {user.home}/{appName}/{directory}/{date-today}/{datetime}.txt
     *      c:/users/kreddy/myapp/logs/2016-03-20/2016-03-20-09-30-45.txt
     *
     * @param directory : The name of the sub directory in the app
     * @param content   : The content to write
     * @return
     */
    fun writeFileForDateAsTimeStamp(directory: String, content: String): String {
        val fileName = DateTime.now().toStringLong()
                .replace(":", "-")
                .replace(" ", "-") + ".txt"
        return writeDatedFile(directory, fileName, content)
    }


    /**
     * writes a file to a date based directory inside an app directory inside the users home directory
     *
     * e.g. {user.home}/{appName}/{directory}/{date-today}/{datetime}.txt
     *      c:/users/kreddy/myapp/logs/2016-03-20/2016-03-20-09-30-45.txt
     *
     * @param directory : The name of the sub directory in the app
     * @param content   : The content to write
     * @return
     */
    fun writeDatedFile(directory: String, fileName: String, content: String): String {
        val dirInfo = createDirectoryForDate(directory)
        val dirPath = dirInfo.second
        val filePath = dirPath + File.separator + fileName
        val file = File(filePath)
        file.writeText(content)
        return filePath
    }


    /**
     * creates a folder inside the app directory of the user home path
     * e.g. {user.home}/{appName}/{directory}/{date}/
     *      c:/users/kreddy/myapp/logs/2016-03-20/
     *
     * @param directory : The name of the sub directory in the app
     * @return
     */
    fun createDirectoryForDate(directory: String): Pair<String, String> {
        // Now create {user.home}/{appName}/{directory}/{date}
        val dateName = folderNameByDate()
        val dateDir = File(directory, dateName)
        if (!dateDir.exists()) {
            dateDir.mkdir()
        }
        return Pair(dateDir.name, dateDir.absolutePath)
    }
}