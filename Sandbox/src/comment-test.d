import std.stdio;

// Don't be confused by /* in the middle of a // comment.
/* Don't be confused by a // in the middle of a /* comment. */
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

