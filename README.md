# html-analyzer-java

## overview

This project has the objective to work as an HTML page analyzer and error identifier, trough a simple analyzing method via string stack

It is a fully designed Java code attached to this readme file, to analyze pages and return a message of error in case any malformation in the HTML that was analyzed. The README file was manually written and designed to work as a tutorial and view on how the code works.

### Specs

-This code must be run in JDK 17 or newer
-The code was created in VSCode, but can be done in any Java IDE of your preference as long as it support Java and it's libraries
-The main goal was keeping it simple: Only native libraries from the JDK set.
-It contains a .java file and a .md file

### How to run the code

1. Run terminal (Most often PowerShell)
2. Compile the code: "javac {NameOfFile}" (for Java, must be the same between file and class inside the file)
    2.1 If successfully ran, continue the process
    2.2 If given any compiling errors or syntax errors, return and verifiy things such as: File-class name similarity, variable/object names, method names and capslock in the correct letters. after fixed, try running it again in case the .class file was not created.
3. After compiling, type the command prompt that will run the .class file along with the complete URL that you wish to be analyzed: "java {URL}" (Example: "java https://google.com")
4. The result will appear on the terminal, possibly returning 3 results:
    4.1: Deepest text it found considering the tags in the HTML file (header 3 as example is deeper than just body)
    4.2: Malformed HTML (Meaning that it contains errors like an open tag. For future versions, I plan to add a identifier to the stack that returns which tag was missing, and which line)
    4.3: URL connection error (In case it couldn't load the URL or had any network errors)


### Technical explanation

-Stack - Piles
The HTMl Analyzer works by using a Stack system that piles tags and values, searching for the deepest value possible (maxDepth) while also saving the value with the deepest level (deepestText). The stack works by methods such as: stack.push(tag) and stack.pop(), also using the .equals(tag) method to analyze the similarity between two identical tags, verifying if their string values match. 

-Structure
The object line was added to represent thhe line analyzed alogn the tags (Analysis via line per line, same as a high-level code interpretation)
The object scanner was added to represent the scanner of the URL itself
The statements are a conditional analysis over the HTML analyzed via the URL (if, else, if else, try, catch, continue, return)
The added logical operators, like || and !, were added to represent a possible OR situation during analysis and a NOT situation in a few objects
The .substring() method works to validate the strings between the texts, helping to identify which is the content inside the tag other than the "<>" symbols. 
The line.length() method works with the substring method to analyze the tags content
The isEmpty() method is to analyze if the stack and/or object line is empty, which will return malformed HTML whenever the stack is NOT empty and will continue if the line is empty (according to conditions)
The deepestText variable represents the string value thatw as analyzed to be the deepest found in the HTML file
The Exception class works with the e variable and the catch statement, in case any part of the code goes wrong not according to the run, as a soft of "emergency measure"
The System.out.println() method prints the result according to the analysis

-Cleaning 
The funcionality added to clean empty spaces was the .trim() method, focused on irrelevant values such as empty spaces between tags, strings and such, valuing only the content actually written in the HTML file analyzed.


