
Libraries
=========

This folder contains third party libraries used by Daja and its related projects and that are
*not* automatically downloaded and managed by SBT. Any jar file deposited here should be picked
up by SBT and included in the classpath of the Daja projects.

The following libraries need to be downloaded manually and put into this folder:

+ The [DocBook v5.1
  Schema](http://docs.oasis-open.org/docbook/docbook/v5.1/os/schemas/rng/docbookxi.rnc). This
  schema includes XInclude support, which is used in the Daja documentation. This is only needed
  if you plan to edit the DocBook documentation.
  