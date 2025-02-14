# frozen_string_literal: true
# shareable_constant_value: literal
baseruby="/usr/bin/ruby --disable=gems"
_\
=begin
_=
ruby="${RUBY-$baseruby}"
case "$ruby" in "echo "*) $ruby; exit $?;; esac
case "$0" in /*) r=-r"$0";; *) r=-r"./$0";; esac
exec $ruby "$r" "$@"
=end
=baseruby
class Object
  remove_const :CROSS_COMPILING if defined?(CROSS_COMPILING)
  CROSS_COMPILING = RUBY_PLATFORM
  constants.grep(/^RUBY_/) {|n| remove_const n}
  RUBY_VERSION = "3.2.2"
  RUBY_RELEASE_DATE = "2023-03-30"
  RUBY_PLATFORM = "x86_64-darwin21"
  RUBY_PATCHLEVEL = 53
  RUBY_REVISION = "e51014f9c05aa65cbf203442d37fef7c12390015"
  RUBY_COPYRIGHT = "ruby - Copyright (C) 1993-2023 Yukihiro Matsumoto"
  RUBY_ENGINE = "ruby"
  RUBY_ENGINE_VERSION = "3.2.2"
  RUBY_DESCRIPTION = case
    when RubyVM.const_defined?(:MJIT) && RubyVM::MJIT.enabled?
      "ruby 3.2.2 (2023-03-30 revision e51014f9c0) +MJIT [x86_64-darwin21]"
    when RubyVM.const_defined?(:YJIT) && RubyVM::YJIT.enabled?
      "ruby 3.2.2 (2023-03-30 revision e51014f9c0) +YJIT [x86_64-darwin21]"
    else
      "ruby 3.2.2 (2023-03-30 revision e51014f9c0) [x86_64-darwin21]"
    end
end
builddir = File.dirname(File.expand_path(__FILE__))
srcdir = "."
top_srcdir = File.realpath(srcdir, builddir)
fake = File.join(top_srcdir, "tool/fake.rb")
eval(File.binread(fake), nil, fake)
ropt = "-r#{__FILE__}"
["RUBYOPT"].each do |flag|
  opt = ENV[flag]
  opt = opt ? ([ropt] | opt.b.split(/\s+/)).join(" ") : ropt
  ENV[flag] = opt
end
