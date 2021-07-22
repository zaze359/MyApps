package com.zaze.apps.ext

import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : ZAZE
 * @version : 2018-12-24 - 14:50
 */
fun String?.i(tag: String = ZTag.TAG_DEBUG) {
    ZLog.i(tag, this)
}