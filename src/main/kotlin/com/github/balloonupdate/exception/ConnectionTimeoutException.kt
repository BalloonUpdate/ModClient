package com.github.balloonupdate.exception

import com.github.balloonupdate.util.Utils

class ConnectionTimeoutException(url: String) : BaseException("连接超时(${Utils.getUrlFilename(url)}): $url")