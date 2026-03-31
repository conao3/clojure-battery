;; Original: Lib/errno.py (module)

(ns conao3.battery.errno)

(def EPERM 1)
(def ENOENT 2)
(def ESRCH 3)
(def EINTR 4)
(def EIO 5)
(def ENXIO 6)
(def E2BIG 7)
(def ENOEXEC 8)
(def EBADF 9)
(def ECHILD 10)
(def EAGAIN 11)
(def EWOULDBLOCK 11)
(def ENOMEM 12)
(def EACCES 13)
(def EFAULT 14)
(def EBUSY 16)
(def EEXIST 17)
(def EXDEV 18)
(def ENODEV 19)
(def ENOTDIR 20)
(def EISDIR 21)
(def EINVAL 22)
(def ENFILE 23)
(def EMFILE 24)
(def ENOTTY 25)
(def EFBIG 27)
(def ENOSPC 28)
(def ESPIPE 29)
(def EROFS 30)
(def EMLINK 31)
(def EPIPE 32)
(def EDOM 33)
(def ERANGE 34)
(def EDEADLK 35)
(def EDEADLOCK 35)
(def ENAMETOOLONG 36)
(def ENOLCK 37)
(def ENOSYS 38)
(def ENOTEMPTY 39)
(def ELOOP 40)
(def ENOMSG 42)
(def EIDRM 43)
(def ENOSTR 60)
(def ENODATA 61)
(def ETIME 62)
(def ENOSR 63)
(def EREMOTE 66)
(def ENOLINK 67)
(def EPROTO 71)
(def EMULTIHOP 72)
(def EBADMSG 74)
(def EOVERFLOW 75)
(def EUSERS 87)
(def ENOTSOCK 88)
(def EDESTADDRREQ 89)
(def EMSGSIZE 90)
(def EPROTOTYPE 91)
(def ENOPROTOOPT 92)
(def EPROTONOSUPPORT 93)
(def ESOCKTNOSUPPORT 94)
(def EOPNOTSUPP 95)
(def EAFNOSUPPORT 97)
(def EADDRINUSE 98)
(def EADDRNOTAVAIL 99)
(def ENETDOWN 100)
(def ENETUNREACH 101)
(def ENETRESET 102)
(def ECONNABORTED 103)
(def ECONNRESET 104)
(def ENOBUFS 105)
(def EISCONN 106)
(def ENOTCONN 107)
(def ESHUTDOWN 108)
(def ETOOMANYREFS 109)
(def ETIMEDOUT 110)
(def ECONNREFUSED 111)
(def EHOSTDOWN 112)
(def EHOSTUNREACH 113)
(def EALREADY 114)
(def EINPROGRESS 115)
(def ESTALE 116)

(def errorcode
  {EPERM "EPERM"
   ENOENT "ENOENT"
   ESRCH "ESRCH"
   EINTR "EINTR"
   EIO "EIO"
   ENXIO "ENXIO"
   E2BIG "E2BIG"
   ENOEXEC "ENOEXEC"
   EBADF "EBADF"
   ECHILD "ECHILD"
   EAGAIN "EAGAIN"
   ENOMEM "ENOMEM"
   EACCES "EACCES"
   EFAULT "EFAULT"
   EBUSY "EBUSY"
   EEXIST "EEXIST"
   EXDEV "EXDEV"
   ENODEV "ENODEV"
   ENOTDIR "ENOTDIR"
   EISDIR "EISDIR"
   EINVAL "EINVAL"
   ENFILE "ENFILE"
   EMFILE "EMFILE"
   ENOTTY "ENOTTY"
   EFBIG "EFBIG"
   ENOSPC "ENOSPC"
   ESPIPE "ESPIPE"
   EROFS "EROFS"
   EMLINK "EMLINK"
   EPIPE "EPIPE"
   EDOM "EDOM"
   ERANGE "ERANGE"
   EDEADLK "EDEADLK"
   ENAMETOOLONG "ENAMETOOLONG"
   ENOLCK "ENOLCK"
   ENOSYS "ENOSYS"
   ENOTEMPTY "ENOTEMPTY"
   ELOOP "ELOOP"
   ENOMSG "ENOMSG"
   EIDRM "EIDRM"
   ECONNREFUSED "ECONNREFUSED"
   ETIMEDOUT "ETIMEDOUT"
   ECONNRESET "ECONNRESET"
   EADDRINUSE "EADDRINUSE"
   ENETUNREACH "ENETUNREACH"})

(defn strerror
  "Returns the error message for errno value."
  [n]
  (case n
    1 "Operation not permitted"
    2 "No such file or directory"
    3 "No such process"
    4 "Interrupted system call"
    5 "Input/output error"
    9 "Bad file descriptor"
    10 "No child processes"
    11 "Resource temporarily unavailable"
    12 "Cannot allocate memory"
    13 "Permission denied"
    17 "File exists"
    20 "Not a directory"
    21 "Is a directory"
    22 "Invalid argument"
    24 "Too many open files"
    28 "No space left on device"
    32 "Broken pipe"
    36 "File name too long"
    39 "Directory not empty"
    98 "Address already in use"
    104 "Connection reset by peer"
    110 "Connection timed out"
    111 "Connection refused"
    (str "Unknown error " n)))
