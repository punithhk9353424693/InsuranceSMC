#ifndef RUBY_MJIT_CONFIG_H
#define RUBY_MJIT_CONFIG_H 1

#ifdef LOAD_RELATIVE
#define MJIT_HEADER_INSTALL_DIR "/include/ruby-3.2.0/x86_64-darwin21"
#else
#define MJIT_HEADER_INSTALL_DIR "/Users/matthewphilips/.rubies/ruby-3.2.2/include/ruby-3.2.0/x86_64-darwin21"
#endif
#define MJIT_MIN_HEADER_NAME "rb_mjit_min_header-3.2.2.h"
#define MJIT_CC_COMMON   "/usr/bin/clang",
#define MJIT_CFLAGS      MJIT_ARCHFLAG "-w", "-fdeclspec",
#define MJIT_OPTFLAGS    "-O3", "-fno-fast-math",
#define MJIT_DEBUGFLAGS 
#define MJIT_LDSHARED    "/usr/bin/clang", "-dynamic", "-bundle",
#define MJIT_DLDFLAGS    MJIT_ARCHFLAG "-L/usr/local/opt/openssl@1.1/lib", "-L/usr/local/opt/readline/lib", "-L/usr/local/opt/libyaml/lib", "-L/usr/local/opt/gdbm/lib", "-Wl,-multiply_defined,suppress",
#define MJIT_LIBS       
#define PRELOADENV       "DYLD_INSERT_LIBRARIES"
#define MJIT_ARCHFLAG    /* no flag */

#endif /* RUBY_MJIT_CONFIG_H */
