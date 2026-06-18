## Basic Git Initialization Commands

##### Clone this repo locally
git clone https://github.com/kch55-sys/UnityRoboTest


## Access the repo

### Get into the folder
cd UnityRoboTest

### Check the current status of your files
git status

### Check branches
git branch -l

### Create your new branch 
git checkout -b <username>/<meaningful branch name>


### CCheckout existing branch 
git checkout <branch name>

### Verify you are in your branch 
git status

### make code changes in editor (Android studio / intellij/ Eclipse)

### Check which files are changed
git status


### Add all files to staging (prepare them for commit)
git add .


### Commit your staged files with a message
git commit -m "<message>"


### When ready to push the new code, validate your branch is upto date to void merge conflict or overwriting files. 
git status  -> this thould have no uncommitted files
git checkout master
git pull
git checkout <your branch>
git rebase master


### Push your first commit to the main branch
git push -u origin master


### Check which files are changed
git status


### Make master upt o date
git checkout master
git pull




