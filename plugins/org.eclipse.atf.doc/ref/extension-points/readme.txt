* Updating the extension point documentation in this directory

- There may be an easier way to do this, but I haven't figured it out. In
order to update the *.html files which contain the extension point docs,
do the following;
	1) Make the doc update to the corresponding extension point's schema 
		file in the plugin where it is declared.
	2) Once you've made the appropriate changes, right click on the schema
		(*.exsd) file and select "PDE Tools->Preview Reference Document".
	3) Once the preview comes up, right click inside the editor and 
		select "View Source".
	4) When the page source comes up, copy everything inside of the <BODY>
		tags and paste it inside of the <BODY> tag of the plugin *.html
		file you are updating in this directory, and save.
		
	You can also directly modify the plugin *.html file if it is a minor
	change.