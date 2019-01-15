# changelog_builder
Automatic changelog &amp; dev tools for release and tracking

This tools provide different things to be more efficient & accurate on a release & tracking process.

This package is used with Jenkins ver. 2.150.1 (optional) Atlassian Bitbucket v5.16.0 (can be replaced with any repository hosting service who provide webhooks).

The process is to follow thoses steps:

Create a hook on the "eventKey":"repo:refs_changed" (Atlassian Bitbucket v5.16.0) and parse the json to get `\$.changes.[0].fromHash`
if this value match
` regexpFilterText('\$HASH') regexpFilterExpression('0000000000000000000000000000000000000000') //to check if it's a new branch`
  it means that we have a new branch so we can trigger the Jenkins job (or whatever you want) to run the script insert_pr.
  
  insert_pr.sh parse the branch to get all information to insert a specific line on the changelog.
  
  the pattern expected  is TYPE/TICKET/ACT like Feature/My-Jira-542/New_route_to_reach_my_jira and can catch some different TYPE
  see the if forest in insert_pr.sh ;)
  
  Create a hook on the "eventKey":"pr:opened" (Atlassian Bitbucket v5.16.0); this is gonna trigger a job (or wahtever you want) to sed the ID of the pr into the changelog to be able to track in the realese process what have be done with wich pr and related to wich JIRA.
  
  And then, when you want to do a release, run change_release_verions.sh who can be trigger on a merge into master if you want.
  
  this job read the Version in progress block to know what have be done and upgrade your next sementic version in purpose.
  
  
  this tool is just a little block who can make easy to have a great changelog who can be use to track, follow & be garant of your sementic version.
  
  This si an example of how it's look like.
  
  https://github.com/Tocard/changelog_builder/blob/master/changelog.adoc
  
  
