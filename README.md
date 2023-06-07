# Scan me! Calculator (Atask Assignment)


The app should allow the user to capture arithmetic expressions (i.e. 1+2) either directly from the built-in camera or from an image file picked by the user from the filesystem. Once the input is provided the app should detect the expression in the picture and compute its result. The result should be stored locally so the user can browse recent operations.

## Requirements:
- input can be provided by using the built-in camera directly or by picking a picture from
the filesystem
- a 3rd party solution can be used to find an expression in a picture
- only very simple 2 argument operations must be supported (+,-,*,/) (i.e. 2+2, 3-1, etc)
- if there are multiple expressions in the picture take the first one
- there should be a screen that allows the user to browse recent results

An important piece of this task is to verify your ability to build multi-flavour apps (behavior of the app controlled at compile and at runtime).
The app should be configurable in the following dimensions:
- theme (compile time)
  - red
  - green
- UI functionality (compile time)
  - only allow to pick the picture from the filesystem
  - only allow to use a built-in camera
- recent results storage engine (run time)
  - store the expression and the result in an encrypted file
  - store the expression and the result in a non-encrypted database

It should be possible to build 4 apks from the delivered source code: 
1. app-red-filesystem.apk
2. app-red-built-in-camera.apk
3. app-green-filesystem.apk
4. app-green-built-in-camera.apk

There should be a switch in the UI that allows switching between the currently used storage engine.
Preferred approach
- use modern Android architecture
- use coroutines if needed

That’s it. All the other decisions are up to you, including the app UI design.
Topics covered by this assignment:
- support of multiple app variants
- controlling the behavior of the app at compile time and at runtime
- handling different themes
- integration with a 3rd party library
- integration with the system (file picker, camera)
- permission handling (file picker, camera)
- writing/reading a file
- dealing with a db
- encryption / decryption
