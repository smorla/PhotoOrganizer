# PhotoOrganizer
A tool for organizing photo and video files based on the EXIF info.

This simple application has been concieved to help you to organize, in your have drive, the insane amount of pictures you have been storing for ages. If you're one of those who has all your pics buried in a de-organized and infinite set of folders, this application may help you.

The application works as a command line tool and is very easy to use.

1st step: specify the folder where the applications will start to search for files (it searches for every file recursively diving in the directories).
2nd step: specify the folder you want to store your organized photo catalog.
3rd step: select what do you want to do with you files. want to copy or move the files?
4th step: select whether you want to simulate the process o you want to execute the proces. Simulate is for those not feeling confident of what is going to happen. Process is for those brave ones!

The process will organize your pictures (jpegs, tiffs, raw files) and videos (avi, mpeg) having EXIF metadata in them by applying the following 'fixed' criteria:

Year -> month of year

The result of the process is a very useful photo catalog orgranization to start building a Lightroom catalog.

Next releases may have the following improvements:
- Do not overwrite existing files in the target folder, which leads to keep just the last file found for a given file name.
- Graphical user interface
- No fixed catalog organization criteria including other options such as device, etc.

