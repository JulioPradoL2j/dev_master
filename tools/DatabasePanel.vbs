Option Explicit

Dim shell, fso
Set shell = CreateObject("WScript.Shell")
Set fso   = CreateObject("Scripting.FileSystemObject")

' Pasta do VBS (tools\)
Dim toolsDir : toolsDir = fso.GetParentFolderName(WScript.ScriptFullName)

' --- Java ---
Dim javaHome, javaExe
javaHome = shell.Environment("PROCESS").Item("JAVA_HOME")
If javaHome <> "" Then
    javaExe = javaHome
    If InStr(LCase(javaExe), "\bin") = 0 Then javaExe = javaExe & "\bin"
    javaExe = javaExe & "\java.exe"
Else
    javaExe = "java"
End If

' --- CP igual ao BAT ---
Dim cp
cp = toolsDir & "\..\libs\*;" & _
     toolsDir & "\..\build\classes;" & _
     toolsDir & "\..\dist\gameserver.jar"

Dim mainClass
mainClass = "net.sf.l2j.tools.database.DatabasePanelLauncher"

Dim command
command = """" & javaExe & """" & " -Xmx512m -cp """ & cp & """ " & mainClass

' roda como o BAT: a pasta atual fica sendo tools\
Dim cmdLine
cmdLine = "cmd /k cd /d """ & toolsDir & """ && title L2JDev Database Panel && " & command

' 1 = console visível (recomendado pra debug)
shell.Run "cmd /c cd /d """ & toolsDir & """ && " & command, 0, False

