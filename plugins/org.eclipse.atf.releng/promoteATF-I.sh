#!/bin/sh

BUILD_LOC=/tmp/atfpromo
ANT=/opt/public/common/apache-ant-1.7.1/bin/ant
DASH_RELENG_DIR=/shared/cbi/build/org.eclipse.dash.common.releng
RELENG_DIR=$BUILD_LOC/org.eclipse.atf/components/releng/org.eclipse.atf.releng

echo "$DATE: getting last successful build" 
mkdir -p $BUILD_LOC
rm -f $BUILD_LOC/build.zip
rm -rf $BUILD_LOC/build
cd $BUILD_LOC
wget --no-check-certificate "https://hudson.eclipse.org/hudson/job/cbi-atf-0.3-nightly/lastSuccessfulBuild/artifact/build/*zip*/build.zip"
if [ ! -f build.zip ]; then echo "ERROR:build.zip (from Hudson) not found"; exit -2; fi
unzip build.zip
echo "$DATE: getting project releng ..."
cvs -d :pserver:anonymous@dev.eclipse.org:/cvsroot/tools co org.eclipse.atf/components/releng/org.eclipse.atf.releng

echo "$DATE: publishing nightly build ..."
$ANT -f $DASH_RELENG_DIR/promote.xml -DrelengCommonBuilderDir=$DASH_RELENG_DIR -Dpromote.properties=$RELENG_DIR/promote-I.properties 2>/dev/null
cd
rm -rf $BUILD_LOC
