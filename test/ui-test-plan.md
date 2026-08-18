# UI Test Plan

## Configuration

- Setup command: `javac -d build/classes src/main/java/Task.java src/main/java/Todo.java src/main/java/Panda.java`
- Run command: `java -cp build/classes Panda`
- Timeout seconds: `10`

## Test Case: Mark a task as done

Aim: Verify that `mark` changes the selected task to done and that `list` displays its new status.

Input:

```text
read book
return book
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
read book
____________________________________________________________
____________________________________________________________
return book
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
read book
return book
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
read book
____________________________________________________________
____________________________________________________________
return book
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
