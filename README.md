# Turing Machine Simulator
Turing Machine simulator in Java, with CLI interface.

## Features so far
  - Run single-tape Turing Machines
  - Save and load machines using .json files
  - Change settings like default machines depository, default blank symbol etc.

## How to set it up
This app WONT work in normal Windows cmd - the colored output isn't supported there. You will need a custom terminal app - I recomend Windows Terminal. It works fine in default Linux terminal.
Make sure you have Java installed. Then, just run the app using 
``` 
java -jar tm-sim-0.1-jar-with-dependencies.jar
```

## How to use it
I included a .zip file with some sample machines you can run and test the app. You can get it in the Releases section.

### Creating a machine
In main menu, type `new` to create a machine.

You will need to enter the alphabet - all the symbols that the machine will recognize. If you won't add the default blank symbol, the app will add it for you. Then press enter to proceed. 


Next you will need to enter all the moves defined for the machine. You will need to enter them like this:

__current_state , char_under_the_head , new_state , new_char , direction__

__current_state__ is the state that the machine need to be to execute the move.

__char_under_the_head__ is the char that need to be under the head to execute the move.

__new_state__ is the state that the machine will go to after the move.

__new_char__ is the char that will replace __char_under_the_head__ after the move.

__direction__ is the direction that the head will move. Allowed symbols are:
* L or < to move left one position,
* R or > to move right one position,
* N or - means the head will stay in place.

After you entered all the moves, type `end` to proceed to next step.

Next you will need to define the end states - if the machine enters one of these steps, if will stop working. You can enter one or multiple states, separated by a coma. After you're done, press enter to proceed.

Lastly, you will need to enter starting state. You can onlu enter one state. 

Then press enter - and the machine is configured. You can run it using `run`, save it using `save` or exit to menu using `menu`.

### Loading and saving machines
If you have saved machines, you can load them by typing `load` in main menu. It will show a menu with all the machines in the directroy (the default directory is /machines).

You can select the machine by typing its name (you can include the .json file extension, but it isn't required) or index (seen on the left).

Once you loaded a machine, you can run tapes on it by typing `run` or save them using `save`. If you choose to save a machine, you will need to give it a name and description (the description is optional). It will be saved to the directory.

### Changing settings
By typing `settings` you can change these settings:
* __display__ changes the amount of symbols displayed at each step. The default value is 10, which means that there are 5 symbols to thle left and 5 symbols to the right.
* __blank__ changes the default blank symbol - the symbol that fills the tape to the left and the right tape. It will be added by default to each new machine's alphabet. The default value is B.
* __directory__ changes the directory in which the machines will be saved. The default value is /machines. If you change this setting, the app will ask you if you wish to move the machines to a new folder.
* __default__ reverts all the settings to default values.

## Feedback
Any feedback is appreciated! I will try to add more features in the future, like multiple tape machines support, saving tapes and running macros. 


