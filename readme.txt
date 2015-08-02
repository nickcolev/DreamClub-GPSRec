Title:	GPS Recorder
Date:	02-08-2015
Scoop:	Android app to record path or make a screenshot of a location

Like a sound recorder, or a camera, that records user's location
instead of audio or image/video. The UI is a list with records.
When user clicks to a "picture/video" -- it is displayed to him.
The snapshots are recorded in a SQLite database. The paths (movement
record) are like frames in the same database. An index keeps track
of the records.

Requirements:
- User to be able to maske a snapshot of the current location
- Ditto record location in a time frame
- List snapshot/records and select one
- Delete a record
- Tag a record
- View a position/path at the map

Database:
toc - recordings index
			t1	- Begin timestamp
			t2	- End timestamp	(for a snapshot equals to 't1')
			tag	- Tags of a snapshot/recording
frame - recorded frames
			ts	- Timestamp
			lat	- Lattitude
			lng	- Longitude
			p	- Precision (Accuracy)
			src	- Source (Provider)
