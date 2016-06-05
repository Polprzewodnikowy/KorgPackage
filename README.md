# KorgPackage

Small program to provide easy way to edit, unpack and pack KORG Professional Arranger series operating system packages.

Supported models:

|model|mach id|product id|
|---|
|Pa600|Z103A|0|
|Pa900|Z104A|1|
|Pa300|Z106A|2|
|Pa3XLe|Z107A|3|
|Havian 30|Z108A|4|
|Pa4X|Z110A|5|

#Supported chunks:

|id|Name|
|---|
|1|header|
|2 - 14|system file|
|15|installer script|
|16|directory|
|18|file|
|19|file system (rootfs)|

#Unsupported chunks:

|id|Name|
|---|
|17|link|

#System files:

|id|type|path|
|---|
|2|update kernel|/update/uImage|
|3|update ramdisk|/update/ramdisk.gz|
|4|update installer app|/update/lfo-pkg-install|
|5|update installer app config|/update/lfo-pkg-install.xml|
|6|service kernel|/service/uImage|
|7|service ramdisk|/service/ramdisk.gz|
|8|service app|/service/lfo-service|
|9|service app config|/service/lfo-service.xml|
|10|update launcher app|/service/lfo-pkg-launcher|
|11|update launcher app config|/service/lfo-pkg-launcher.xml|
|12|1st stage bootloader (x-loader)|/boot/MLO|
|13|2nd stage bootloader (u-boot)|/boot/u-boot.bin|
|14|user kernel|/kernel/uImage|

#Header structure:

|type|size|name|
|---|
|int|4|id|
|int|4|chunk size|
|byte|12|unknown bytes|
|string||mach_id and customization|
|string||build system 1|
|string||build system 2|
|string||creation date|
|string||creation time|
|string||package type 1|
|string||package type 2|

#System file structure:

|type|size|name|
|---|
|int|4|id|
|int|4|chunk size|
|byte|16|MD5 sum of data|
|byte|data size|data|

`data size = chunk size - 16`

#Installer script structure:

|type|size|name|
|---|
|int|4|id|
|int|4|chunk size|
|byte|16|MD5 sum of data|
|short|2|order|
|string||name|
|byte|data size|data|

`data size = chunk size - 16 - 2 - (length of name + 1)`

#Directory structure:

|type|size|name|
|---|
|int|4|id|
|int|4|chunk size|
|short|2|owner|
|short|2|group|
|short|2|attributes|
|short|2|order|
|string||path|

#File structure:

|type|size|name|
|---|
|int|4|id|
|int|4|chunk size|
|byte|16|MD5 sum of data|
|short|2|owner|
|short|2|group|
|short|2|attributes|
|short|2|order|
|int|4|data size|
|byte|1|compression type|
|string||path|
|string||date|
|string||time|
|data|||

if compression type == 0 (raw data):

|type|size|name|
|---|
|byte|data size|data|

else if compression type == 1 (zlib compression):

each file is divided in 1MB or smaller blocks

block type == 0x00000100:

|type|size|name|
|---|
|int|4|block type|
|int|4|compressed block size|
|int|4|uncompressed block size (reversed byte order)|
|byte|compressed block size - 4|data|

block type == 0x00000101:

|type|size|name|
|---|
|int|4|block type|
|int|4|0x00000000|

`block type = 0x00000100 -> data block`
`block type = 0x00000101 -> ending block`

#File system structure:

|type|size|name|
|---|
|int|4|id|
|int|4|chunk size|
|byte|16|MD5 sum of data|
|int|4|data size|
|short|2|0x0002|
|string||path|
|byte|data size|data|

#Attributes

`ATTR_VFAT_ARCHIVE`
`ATTR_VFAT_READONLY`
`ATTR_VFAT_SYSTEM`
`ATTR_VFAT_HIDDEN`
`ATTR_EXT3_OWNER_R`
`ATTR_EXT3_OWNER_W`
`ATTR_EXT3_OWNER_X`
`ATTR_EXT3_GROUP_R`
`ATTR_EXT3_GROUP_W`
`ATTR_EXT3_GROUP_X`
`ATTR_EXT3_OTHER_R`
`ATTR_EXT3_OTHER_W`
`ATTR_EXT3_OTHER_X`
`ATTR_EXT3_DONT_CHANGE`