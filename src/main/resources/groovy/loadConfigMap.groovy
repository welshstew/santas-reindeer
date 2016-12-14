
/**
 * Created by swinchester on 14/12/2016.
 */

import groovy.io.FileType

def list = []

def dir = new File("/etc/santas-config")
dir.eachFileRecurse (FileType.FILES) { file ->
    list << file
}

list.each {
    println it.path
}