# UI Test Plan

## Configuration

- Setup command: `javac -d build/classes src/main/java/Task.java src/main/java/Todo.java src/main/java/Deadline.java src/main/java/Event.java src/main/java/Panda.java`
- Run command: `java -cp build/classes Panda`
- Timeout seconds: `10`

## Test Case: Mark a task as done

Aim: Verify that `mark` changes the selected task to done and that `list` displays its new status.

Input:

```text
todo read book
todo return book
mark 2
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][X] return book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Unmark a completed task

Aim: Verify that `unmark` reverses a completed task's status and that `list` displays it as unfinished.

Input:

```text
todo read book
todo return book
mark 2
unmark 2
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [ ] return book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add a date-free to-do

Aim: Verify that `todo` creates a Todo task, confirms its creation, and displays its type and unfinished status.

Input:

```text
todo borrow book
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add a deadline without date validation

Aim: Verify that `deadline` creates a Deadline task and preserves the supplied date/time text in confirmations and lists.

Input:

```text
todo read book
deadline submit report /by 11/10/2019 5pm
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: 11/10/2019 5pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] submit report (by: 11/10/2019 5pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add events with start and end values

Aim: Verify that `event` creates Event tasks and preserves both supplied date/time values in confirmations and lists.

Input:

```text
event team project meeting /from 2/10/2019 2pm /to 4pm
event orientation week /from 4/10/2019 /to 11/10/2019
list
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
Got it. I've added this task:
  [E][ ] team project meeting (from: 2/10/2019 2pm to: 4pm)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] team project meeting (from: 2/10/2019 2pm to: 4pm)
2.[E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject a to-do with no description

Aim: Verify that `todo` without a description reports a panda-themed error and does not add a task.

Input:

```text
todo
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! This panda needs a todo description before it can get to work.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject an unknown command

Aim: Verify that an unrecognized command reports a panda-themed error and does not add a task.

Input:

```text
blah
bye
```

Expected output:

```text
____________________________________________________________
 ____    _    _   _ ____    _
|  _ \  / \  | \ | |  _ \  / \
| |_) |/ _ \ |  \| | | | |/ _ \
|  __// ___ \| |\  | |_| / ___ \
|_|  /_/   \_\_| \_|____/_/   \_\

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
OOPS!!! I'm bamboo-zled; I don't know what that means :-(
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
