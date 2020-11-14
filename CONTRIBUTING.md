# Contributing to Timesheets by Team Code Flow

## Commit message format

`git commit -m "<type>/<user_story_id>:<summary>" -m "<body>"`

e.g. `git commit -m "FIX/US-4: fix form validation in add new project form"

A commit title should be short and precise, no more than 80 characters (it's okay to estimate). 

A commit title must start with a type. 

**BUILD**: Changes that affect the build system or external dependencies
**CI**: Changes to our CI configuration files and scripts
**DOCS**: Documentation only changes
**FEAT**: A new feature
**FIX**: A bug fix
**PERF**: A code change that improves performance
**STYLE**: Formatting, missing semi colons, ...
**REFACTOR**: A code change that neither fixes a bug nor adds a feature, but it does more than just changes styling.
**TEST**: Adding missing tests or correcting existing tests
**CHORE**: Anything else

A commit title should include a user story id, if there is one.

The commit title should include a short summary of the commit.
* in present first tense (i.e. fix, instead of fixes or fixed)
* no capitalisation
* no period in the end

A commit body is not mandatory, but you may add one if you think that it's necessary. You may write in full sentences but
try to be precise.

## Branch names

`git checkout -b <your_name>/<type>-<user_story_id>-<branch-name>`

e.g. `git checkout -b aija/FEAT-US-5-add-employee-to-project`

Use the same format at the beginning as with commits.

Use hyphens (-) in your branch name, not underscores (_).
