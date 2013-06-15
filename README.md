pgm-processor
=============
A graphical program for basic PGM image processing, written in Java.
This program  has been written to get a basic understanding of tasks related to
the field of computer vision.

Features
----------
- reading P5 images (grayscale, binary content)
- displaying pgm images and histograms
- color inversion
- blurring
- edge detection
- line detection (experimental)
- saving pgm images


Usage
-------
The program has been written on Linux and is not tested on other operating systems.
You can re-build the project for your OS using maven, however this has not been
tested and is not guaranteed to work.

- go to the ``target`` folder and run the executable jar via::

    java -jar image-processing-0.0.3-SNAPSHOT.jar

- open any P5 PGM image via the window menu ``File-open...``
- apply the smooth/blur effect via ``Functions-smoothen`` and select the 
  properties for the convolution kernel in the following dialog that pops up.
- apply color inversion via ``functions-invert``
- The histogram and preview are updated automatically on each change
- edge detection can be applied via ``functions-edge detection``. You may choose
  between prewitt and Laplace of Gaussian (LoG) as filters, however the LoG filter
  is only available in the sizes 3x3 and 5x5 pixels.
- line detection can be done via ``functions-hough transform``. This feature is
  experimental and does not provide accurate results - see *known issues*.
  Accumulator images are automaticaly stored in the programs root folder for
  debugging.

Source code
-------------
- The lates version of this software can be
  obtained from https://github.com/schocco/pgm-processor
- Feel free to report bugs or send feature requests

Build
--------
A runnable jar can be created with maven by running ``mvn assembly:assembly``
in the ``com.is-gr8.image-processing`` folder.

Unit Tests
------------
Basic functionality is covered with unit tests. After running the unit tests,
modified images are stored in the ``src/test/resources/processed`` folder for
manual review.

Eclipse import
----------------
This folder can be imported as an Eclipse project after running
``mvn eclipse:eclipse`` in the ``com.is-gr8.image-processing``
folder (this will automatically retrieve required dependencies)


Known issues
---------------
- see https://github.com/schocco/pgm-processor/issues
