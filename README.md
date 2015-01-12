<html>

<body>

# 
<a NAME="top"></a>Sun Certified Developer for the Java 2 Platform: Application
Submission (Version 2.3.1)

## 
Introduction and Index

This document tells you what you need, and what you must do, to submit
your solution to the Sun Certified Developer for the Java 2 Platform programming
assignment. Read it carefully before you begin work on the solution. This
document contains strict guidelines about the way the work is to be performed.&nbsp;
These guidelines ensure consistency and fairness.
<p>The application distribution is composed of:

*   This document
*   A non-relational database file
Be sure to maintain a backup copy of all files related to your project,
including the distribution files, until you receive your certificate in
case one or more is corrupted, lost, or becomes unusable. You must not
use any materials issued to other certification candidates even if you
believe they are identical.
<p>This document is divided into the following sections:

*   **[Application Overview](#what_this)** - A general description
of the application you will write
*   **[Architecture](#arch)** - The parts of the application
and how they fit together
*   **[User Interface](#UI)** - GUI specification
*   **[Server](#database)** -&nbsp; The data server and its network
connection
*   **[Data file format](#data)** The format and schema of the database file
*   **[Deliverables](#Deliverables)** - The components you must
deliver, and how you should package them.
*   **[Marking](#marking)** - How your assignment will be graded
*   **[What to do if you have a question](#whattodo)**

### 
Important Note About Automatic Failure

_Where this document uses the word "must" an absolute requirement is
being described. If you fail to adhere to such a requirement, your assignment
will be failed automatically, and without further evaluation. It is therefore
imperative that you pay close attention to any statement using the word
"must" in this document.&nbsp; Portions of your submission will be analyzed
by software; where a specific spelling or structure is required, even a
slight deviation could result in automatic failure.&nbsp;_

* * *

## 
<a NAME="what_this"></a>Application Overview

### Background

Bodgitt and Scarper, LLC. is a broker of home improvement contractors.
They take requests from home owners for a type of service, and offer
the homeowner one or more contractors that can provide the required services.
Curently, Bodgitt and Scarper provides this service over the phone using a team of
customer service representatives (CSRs). The CSRs interact with an
ageing custom-written application that has been drawing increasing criticism
from the CSRs. In the future, Bodgitt and Scarper wants to move into
Internet-based marketing, and hopes to be able to provide their services
directly to customers over the web.
<P>The company's IT director has decided to migrate the existing
application to a Java technology based system. Initially, the system
will support only the CSRs, although the hope is that this interim
step will give them a starting point for migrating the system to the
web. The IT director does not anticipate much reuse of the first Java
technology system, but intends to use that system as a learning
exercise before going on to a web based system.
<P>The company's IT department has a data file that contains the
essential information for the company, but because the data must
continue to be manipulated for reports using another custom-written
application, the new system must reimplement the database code from
scratch without altering the data file format.
<P>The new application, using the existing data file format, must
allow the CSRs to generate a list of constractors that match a
customer's criteria. This is the project that you have been
commissioned to implement.

### 
What you must do

The following are the "top level" features that must be implemented:

*   A client program with a graphical user interface that connects to the database
*   A data access system that provides record locking and a flexible search
mechanism
*   Network server functionality for the database system
The work involves a number of design choices that have to be made. In all
such cases, the following principles should be applied.

#### 
Clarity and Maintainability

<dl>
<dd>
A clear design, such as will be readily understood by junior programmers,
will be preferred to a complex one, even if the complex one is a little
more efficient. Code complexity, including nesting depth, argument passing,
and the number of classes and interfaces, should be reasonable.</dd>
<dd>
</dd>
</dl>

#### 
Documentation

<dl>
<dd>
The code itself should be as clear as possible; do not provide comments
that do not add to the comprehensibility of the code. Awkward or complex
code should have descriptive comments, and javadoc style comments must
be used for each element of the public interface of each class. You must
create a full suite of documentation for the classes of the completed project.
This must be generated using the tool "javadoc" and must be in HTML format.&nbsp;
Provide javadoc documentation for all classes you write.</dd>
<dd>
</dd>
<dd>
You must provide basic user documentation. This should be sufficient to
allow a user who is familiar with the broad purpose of the project to use
the application. This documentation must be in one of these three formats:</dd>
</dl>

*   HTML
*   Plain text (not a wordprocessor format)
*   Within the application as a help system.

#### 
Correctness

<dt>
Your project must conform to this specification.&nbsp; Features that deviate
from specification will not receive full credit.&nbsp; You will not receive
extra credit points for work beyond the requirements of the specification.</dt>

#### 
Use of Standard Elements

<dl>Use of functionality provided by the core Java classes will be preferred
to your own implementation of that functionality, unless there is a specific
advantage to providing your own implementation.</dl>
_[Return to top](#top)_

* * *

## 
<a NAME="arch"></a>Overall Architecture

### 
Major Components

The main architecture of the application must be a traditional client-server
system. There are three key parts:&nbsp; the server-side data management
system, the client-side GUI, and the network connection between the two.

### 
Non-Networked Mode

The program must be able to work in a non-networked mode. In this mode,
the database and GUI must run in the same VM and must perform no networking,
must not use loopback networking, and must not involve the serialization
of any objects when communicating between the GUI and database elements.
<p>The operating mode is selected using the single command line argument
that is permitted. Architecturally, this mode must use the database and
GUI from the networked form, but must not use the network server code at
all.

### 
Network Communication Approach

You have a choice regarding the network connection protocol. You must use
either serialized objects over a simple socket connection, or RMI.&nbsp;
Both options are equally acceptable.&nbsp; Keep in mind that networking
must be entirely bypassed in the non-networked mode.

### 
Restrictions on RMI

To avoid unnecessary complexity in the marking environment certain restrictions
are placed on solutions that use RMI.&nbsp; Specifically:

&nbsp;
<li>
&nbsp;You must not require the use of an HTTP server.</li>
<li>
&nbsp;You must not require the installation of a security manager.</li>
<li>
&nbsp;You must provide all classes pre-installed so that no dynamic class
downloading occurs.</li>
<li>
&nbsp;You must use RMI over JRMP (do not use IIOP)</li>
<p>_[Return to top](#top)_

* * *

## 
<a NAME="UI"></a>The User Interface

The user interface for this assignment must satisfy the following criteria:

*   It must be composed exclusively with components from the Java Foundation
Classes (Swing components).
*   It must allow the user to search the data for all records, or for records
where the name and/or location fields exactly match values specified by
the user.
*   It must present search results in a JTable.
*   It must allow the user to book a selected record, updating the database
file accordingly.
Your user interface should be designed with the expectation of future functionality
enhancements, and it should establish a framework that will support this
with minimal disruption to the users when this occurs.
<p>_[Return to top](#top)_

* * *

## 
<a NAME="database"></a>Server

### Required Interface

Your data access class must be called "Data.java", must be in a package called "suncertify.db", and must implement the following interface:
<P><PRE>
package suncertify.db;
public interface DBMain {
// Reads a record from the file. Returns an array where each
// element is a record value.
public String [] read(int recNo) throws RecordNotFoundException;
// Modifies the fields of a record. The new value for field n 
// appears in data[n].
public void update(int recNo, String [] data)
throws RecordNotFoundException;
// Deletes a record, making the record number and associated disk
// storage available for reuse. 
public void delete(int recNo) throws RecordNotFoundException;
// Returns an array of record numbers that match the specified
// criteria. Field n in the database file is described by
// criteria[n]. A null value in criteria[n] matches any field
// value. A non-null  value in criteria[n] matches any field
// value that begins with criteria[n]. (For example, "Fred"
// matches "Fred" or "Freddy".)
public int [] find(String [] criteria)
throws RecordNotFoundException;
// Creates a new record in the database (possibly reusing a
// deleted entry). Inserts the given data, and returns the record
// number of the new record.
public int create(String [] data) throws DuplicateKeyException;
// Locks a record so that it can only be updated or deleted by this client.
// If the specified record is already locked, the current thread gives up
// the CPU and consumes no CPU cycles until the record is unlocked.
public void lock(int recNo) throws RecordNotFoundException;
// Releases the lock on a record. 
public void unlock(int recNo) throws RecordNotFoundException;
// Determines if a record is currenly locked. Returns true if the
// record is locked, false otherwise.
public boolean isLocked(int recNo)
throws RecordNotFoundException;
}
</PRE>
<P>Any unimplemented exceptions in this interface must all be created as member classes of the
<tt>suncertify.db</tt> package. Each must have a zero argument constructor and a second
constructor that takes a String that serves as the exception's description.
<p>Any methods that throw RecordNotFoundException should do so if a specified
record does not exist or is marked as deleted in the database file.

### 
<font size=+0>Network Approaches</font>

Your choice of RMI or serialized objects will not affect your grade, but
no other approach is acceptable. In either case, the program must allow
the user to specify the location of the database, and it must also accept
an indication that a local database is to be used, in which case, the networking
must be bypassed entirely. No authentication is required for database access.

### 
Locking

Your server must be capable of handling multiple concurrent requests, and
as part of this capability, must provide locking functionality as specified
in the interface provided above.&nbsp; You may assume that at any moment,
at most one program is accessing the database file; therefore your locking
system only needs to be concerned with multiple concurrent clients of your
server.&nbsp; Any attempt to lock a resource that is already locked should
cause the current thread to give up the CPU, consuming no CPU cycles until
the desired resource becomes available.
<p>_[Return to top](#top)_

* * *

##  <a NAME="data"></a> Data file Format

The format of data in the database file is as follows:
<p>Start of file

4 byte numeric, magic cookie value. Identifies this as a data file

4 byte numeric, total overall length in bytes of each record

2 byte numeric, number of fields in each record
<p>Schema description section.

Repeated for each field in a record:

2 byte numeric, length in bytes of field name

n bytes (defined by previous entry), field name

2 byte numeric, field length in bytes

end of repeating block
<p>Data section.

Repeat to end of file:

1 byte "deleted" flag. 0 implies valid record, 1 implies deleted record

Record containing fields in order specified in schema section, no separators
between fields, each field fixed length at maximum specified in schema
information
<p>End of file
<p>All numeric values are stored in the header information use the formats
of the DataInputStream and DataOutputStream classes. All text values, and
all fields (which are text only), contain only 8 bit characters, null terminated
if less than the maximum length for the field. The character encoding is
8 bit US ASCII.

### Database schema

The database that Bodgitt and Scarper uses contains the following fields:
<table>
<tr>
<td>Field descriptive name</td>
<td>Database field name</td>
<td>Field length</td>
<td>Detailed description</td>
</tr>
<tr>
<td>Subcontractor Name</td>
<td>name</td>
<td>32</td>
<td>The name of the subcontractor this record relates to.</td>
</tr>
<tr>
<td>City</td>
<td>location</td>
<td>64</td>
<td>The locality in which this contractor works</td>
</tr>
<tr>
<td>Types of work performed</td>
<td>specialties</td>
<td>64</td>
<td>Comma separated list of types of work this contractor can perform.</td>
</tr>
<tr>
<td>Number of staff in organization</td>
<td>size</td>
<td>6</td>
<td>The number of workers available when this record is booked</td>
</tr>
<tr>
<td>Hourly charge</td>
<td>rate</td>
<td>8</td>
<td>Charge per hour for the subcontractor. This field includes the currency symbol</td>
</tr>
<tr>
<td>Customer holding this record</td>
<td>owner</td>
<td>8</td>
<td>The id value (an 8 digit number) of the customer who has booked this.
Note that for this application, you should assume that customers and CSRs 
know their customer ids. The system you are writing does not interact with 
these numbers, rather it simply records them. If this field is all blanks, the 
record is available for sale.</td>
</tr>
</table>
<p>_[Return to top](#top)_

* * *

## 
<a NAME="Deliverables"></a>Deliverables

### 
Target Platform and Execution

Throughout this exercise, you must use exclusively the Java 2 platform.
You may develop your code using any implementation of the Java 2 platform,
but the submission that you return must have been tested and shown to work
under a production (not development) version of the Sun Microsystems' Java
2 platform and that platform must not have been superseded by a new production
version for more than 18 months by the time you make your submission.
<p>You are permitted to use any IDE tool you choose, but you must not submit
any code that is not your own work. The final program must have no dependencies
on any libraries other than those of the Java 2 Platform.
<p>When you submit your assignment, each part (client and server) must
be executable using a command of this exact form:
> <tt>java -jar &lt;path_and_filename> [&lt;mode>]</tt>
Your programs must not require use of command line arguments other than
the single mode flag, which must be supported.&nbsp; Your programs must
not require use of command line property specifications. All configuration
must be done via a GUI, and must be persistent between runs of the program.
Such configuration information must be stored in a file called suncertify.properties
which must be located in the current working directory.
<p>The mode flag must be either "server", indicating the server program
must run, "alone", indicating standalone mode, or left out entirely, in
which case the network client and gui must run.
<p>You must not require manual editing of any files by the examiners.

### 
Packaging of Submissions

All elements of your submission must be packaged in a single JAR file.
The JAR file must have the following layout and contents in its root:

*   The executable JAR containing the programs. This must be called <tt>runme.jar</tt>.
*   The original, _unchanged_ database file that was supplied to you.
Note that you must keep a copy of the original database file supplied to
you, and this must be the file you submit. The marking process will expect
the exact same data without any changes.
*   A directory called <tt>code</tt>, containing all the source code and related
parts of your project. You must create subdirectories within this to reflect
your package structure and distribute your source files within those directories.
*   A file called <tt>version.txt.</tt> This must contain pure ASCII (not a
word processor format) indicating the exact version of JDK you used, and
the host platform you worked on.
*   A directory called <tt>docs</tt>, containing the following items at the
top level:
<p>_[Return to top](#top)_

* * *

<center>

## 
<a NAME="marking"></a>Marking
</center>
This section describes how your submission will be marked, and the marking
criteria which govern allocation of marks for the Sun Certified Developer
for the Java 2 platform application submission. The first part describes
the marking process, and the second describes how the marks are allocated.
<p>**How The Assignment is Marked**
<p>The marking is done in three phases.&nbsp; First, software checks that
overall structure and nomenclature conform to specification.&nbsp; Second
the examiner runs the code ensuring that it functions correctly through
the specified operations. If any automatic failures are noted at this stage,
the marking process terminates and the assignment is failed.
<p>Provided the essential behavioral requirements of the assignment have
been correctly implemented, the examiner proceeds to investigate the design
and implementation of your assignment. This process is time consuming,
and it is because this is done carefully and thoroughly that submissions
take time to grade. The grading process is closely controlled to ensure
consistency and fairness, and it is performed according to criteria detailed
in the next section. At any time during this process, if an automatic failure
is noted, the marking process terminates, and the assignment is failed.
For any design choice concerning topics not specifically described in the
requirements, marks are awarded for a clear and consistent approach, rather
than for any particular solution. Design decisions must be described _briefly
but clearly_ in your comments.
<p>In addition to the submission, you will be required to take a written
examination. This exam tests your understanding of your submission and
asks you to justify a number of design choices embodied in that submission.

### 
Automatic Failures

_As noted at the beginning of this document, where this document uses
the word "must" an absolute requirement is being described. If you fail
to adhere to such a requirement, your assignment will be failed automatically,
and without further evaluation. It is therefore imperative that you pay
close attention to any statement using the word "must" in this document._

### 
**Marking Criteria**

Your work will be evaluated based on the following criteria. The minimum
passing score is 320 out of a possible 400 points.
<p>General Considerations (100 points)

Documentation (70 points)

Object-orietned design (30 points)

User Interface (40 points)

Locking (80 points)

Data class (40 points)

Server (40 points)

&nbsp;

&nbsp;
<p>_[Return to top](#top)_

* * *

<center>

## 
<a NAME="whattodo"></a>What to do if you have a question
</center>
You might find that you want to ask for further explanation of some part
of this document, perhaps to seek permission to solve a problem in a particular
way. This document deliberately leaves some issues unspecified, and some
problems unraised. Your ability to think through these issues, in the face
of realistically imperfect specifications, and come to a tenable solution
is something upon which you are being graded.
<p>In general, you should _not_ ask your question; rather you should
consider the options available and make a decision about how to address
the problem yourself. This decision-making process is part of the marking
scheme, and as such it is crucially important that you provide documentation
of your choice. Be sure to describe the options you considered, the perceived
benefits and weaknesses of each, and why you chose the solution you did.
Provided you do not contravene any specification in this document you will
not be marked on the particular choice that you made, but rather on the
consistency of your decision making process and your adherence to other
aspects of these notes during that decision making process.
<p>If you feel you must ask your question, you should address it to who2contact@sun.com.
Clearly indicate that the question relates to the Sun Certified Developer
Exam, provide your candidate ID number, name, and include your return email
address in the _body_ of your message. Describe your issue as briefly
as reasonably possible; you will be asked for more information if necessary.
<p>_[Return to top](#top)_
</body>
</html>