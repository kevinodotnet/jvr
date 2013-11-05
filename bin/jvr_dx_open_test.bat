@echo off

@rem ////////////////////////////////////////////////////////////
@rem // Configuration 
@rem ////////////////////////////////////////////////////////////

@rem Set this to the location of your Java installation.
set JAVA_HOME=c:\j2sdk1.4.2_01

@rem Set this to the location of your JVR installation.  The
@rem default value of ".." means the parent directory, which 
@rem will work so long as you invoke the batch file using
@rem Explorer, or from the "bin" directory.
set JVR_HOME=..

@rem ////////////////////////////////////////////////////////////
@rem // You should not need to change anything below this line.
@rem ////////////////////////////////////////////////////////////

@rem JVR required only one JAR file.  When you write your own
@rem application you will need to add your own JAR files as well.
set CLASSPATH=%JVR_HOME%\lib\jvr.jar;lib\jvr.jar

@rem The 'java.library.path' paramater tells the JVM what
@rem directories to look in for shared libraries.  In this case
@rem we specify the "lib" directory so that the JVM can find
@rem jvr.dll
set JAVA_ARGS=-Djava.library.path=%JVR_HOME%\lib;lib

@rem The classpath argument.
set JAVA_ARGS=%JAVA_ARGS% -cp %CLASSPATH%

@rem Invoke the console
%JAVA_HOME%\bin\java %JAVA_ARGS% net.threebit.jvr.test.DxOpenTest

