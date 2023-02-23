# Fetch-Coding-Exercise
This repository contains my coding exercise for Fetch. To make it I used Java and the OpenCSV library. I hope you like it!

<h2>How to Run</h2>
You can run this code through your terminal.

<h3>First, make sure you have Java and JVM installed on your computer.</h3>
You can check by typing java -version and javac -version into your terminal

<h3> Then, clone this repository and run the following commands</h3>
javac TransactionAccounter.java <br>
java TransactionAccounter &lt;points&gt; &lt;csv file&gt;
<br>
<h3> To run the tester methods, run the following commands</h3>
If you are using bash(mac and linux): export CLASSPATH=./opencsv-5.7.1.jar:$CLASSPATH <br>
If you are using swift(windows): $env:CLASSPATH="./opencsv-5.7.1.jar;" + $env:CLASSPATH <br>
javac TransactionTester.java <br>
java TransactionTester
