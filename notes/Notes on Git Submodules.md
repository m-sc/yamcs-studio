This project includes some external CS-Studio submodules to make it easier to integrate documentation and write scripts that work on everyone's setup.

You should read up on gitmodules if you plan on using its functionality, or if you want to try out some changes to the source-code contained therein.

All of the current CS-Studio repositories have their commit ID pointing to a recent commit on their master branch. I tried using the 4.0.x branch everywhere but that crashed on multiple locations in fresh installs.

From time to time these commit-references should be fast-forwarded by the maintainer of Yamcs Studio. (The shared configuration for git submodules cannot follow a branch, only an exact commit).

Be careful not to push commits with commit-references that exist only locally in your workspace (this was one of the reasons not to pursue using the 4.0.x branch, as we'd have to fork the stuff to a shared location).