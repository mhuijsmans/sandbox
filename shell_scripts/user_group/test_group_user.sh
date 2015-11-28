#!/bin/bash 

USER=martien
GROUP=martien

USERBLA=blablablew
GROUPBLA=blablablow

echo "################################"
echo "# Group to which user belongs"
groups $USER

# Listing all groups
# getent group $GROUOP

echo "################################"
echo "# if group doesn't exit, print message"
getent group $GROUPBLA >/dev/null || echo "Group $GROUPBLA does not exists"

echo "################################"
echo "# if user doesn't exit, print message"
getent passwd $USERBLA >/dev/null || echo "User $USERBLA does not exists"