mkdir -p $HOME/tools
if [ ! -f $HOME/tools/phantomjs ]; then

	unamestr=`uname`
	if [[ "$unamestr" == 'Linux' ]]; then
		phantomjs_file=phantomjs-1.9.8-linux-i686.tar.bz2
		phantomjs_dir=phantomjs-1.9.8-linux-i686
		wget --no-check-certificate https://bitbucket.org/ariya/phantomjs/downloads/$phantomjs_file -O $phantomjs_file
		tar xvjf $phantomjs_file
	elif [[ "$unamestr" == 'Darwin' ]]; then
		phantomjs_file=phantomjs-1.9.8-macosx.zip
		phantomjs_dir=phantomjs-1.9.8-macosx
		wget https://bitbucket.org/ariya/phantomjs/downloads/$phantomjs_file -O $phantomjs_file --no-check-certificate
		unzip $phantomjs_file
	fi
	mv $phantomjs_dir/bin/phantomjs $HOME/tools
	rm -Rf $phantomjs_dir
	rm -f $phantomjs_file
	chmod +x $HOME/tools/phantomjs
fi
$HOME/tools/phantomjs -v