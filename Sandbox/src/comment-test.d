import std.stdio;

// Don't be confused by /* in the middle of a // comment.
/* Don't be confused by a // in the middle of a /* comment. */

The following lines should all be blank (except for test numbers).
---START---
1: /+ This is a comment. /+ This is a nested comment. +/ This is a comment. +/
2: /+ ... /+
   Inner stuff!
   +/+/
3: /+/ This is a comment. +/
4: // /+ ... +/ This is still a comment.
5: /* /+ ... +/ This is still a comment. */
6: /++++++/
7: /++//+/++/+/
---END---

int main( )
{
  char ch;
  int  x, i, j;

  writeln( "Hello! // This is not a comment." );
  writeln( "Hello! \" /* This is not a comment." );

  ch = '// This is not a comment.';
  ch = '\' /* This is not a comment.';
  ch = '\''; // This is a comment!
  ch = '"';  /* This is a comment! */
  writeln( "'" /* This is a comment! */ );
  x = i/'z'; // This is a comment!
  i/* There are two tokens here, not a single 'ij' identifier */j

  x = i//* What should happen here? */j
    ;

  /*/*//**/// What should happen here?
  /***/// What should happen here?

  /* What should happen here?
    //*/x=///*
    i+j;

  return 0;
}

    /* This is an enclosed comment that starts on column 5 of line 48
