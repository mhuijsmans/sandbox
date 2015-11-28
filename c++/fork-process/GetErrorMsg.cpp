#include <errno.h>
#include <string.h>

#include "GetErrMsg.h"

std::string GetErrMsg(int errnum)
{

    switch ( errnum ) {

#ifdef EACCES
        case EACCES :
        {
            return "EACCES Permission denied";
        }
#endif

#ifdef EPERM
        case EPERM :
        {
            return "EPERM Not super-user";
        }
#endif

#ifdef E2BIG
        case E2BIG :
        {
            return "E2BIG Arg list too long";
        }
#endif

#ifdef ENOEXEC
        case ENOEXEC :
        {
            return "ENOEXEC Exec format error";
        }
#endif

#ifdef EFAULT
        case EFAULT :
        {
            return "EFAULT Bad address";
        }
#endif

#ifdef ENAMETOOLONG
        case ENAMETOOLONG :
        {
            return "ENAMETOOLONG path name is too long     ";
        }
#endif

#ifdef ENOENT
        case ENOENT :
        {
            return "ENOENT No such file or directory";
        }
#endif

#ifdef ENOMEM
        case ENOMEM :
        {
            return "ENOMEM Not enough core";
        }
#endif

#ifdef ENOTDIR
        case ENOTDIR :
        {
            return "ENOTDIR Not a directory";
        }
#endif

#ifdef ELOOP
        case ELOOP :
        {
            return "ELOOP Too many symbolic links";
        }
#endif

#ifdef ETXTBSY
        case ETXTBSY :
        {
            return "ETXTBSY Text file busy";
        }
#endif

#ifdef EIO
        case EIO :
        {
            return "EIO I/O error";
        }
#endif

#ifdef ENFILE
        case ENFILE :
        {
            return "ENFILE Too many open files in system";
        }
#endif

#ifdef EINVAL
        case EINVAL :
        {
            return "EINVAL Invalid argument";
        }
#endif

#ifdef EISDIR
        case EISDIR :
        {
            return "EISDIR Is a directory";
        }
#endif

#ifdef ELIBBAD
        case ELIBBAD :
        {
            return "ELIBBAD Accessing a corrupted shared lib";
        }
#endif
        
        default :
        {
            std::string errorMsg(strerror(errnum));
            if ( errnum ) return errorMsg;
        }
     }
}
                