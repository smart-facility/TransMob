#!/usr/bin/env bash

# TransMob is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# TransMob is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser Public License for more details.

# You should have received a copy of the GNU Lesser Public License
# along with TransMob.  If not, see <http://www.gnu.org/licenses/>.

cp data/environment.properties.orig data/environment.properties

# From http://shrubbery.mynetgear.net/c/display/W/Reading+Java-style+Properties+Files+with+Shell
# Converts . to _ in the Java .properties file and then uses these as environment variables
TEMPFILE=$(mktemp)
cat data/environment.properties | awk 'BEGIN { FS="="; } \
/^\#/ { print; } \
!/^\#/ { if (NF >= 2) { n = $1; gsub(/[^A-Za-z0-9_]/,"_",n); print n "=\"" $2 "\""; } else { print; } }' \
 >$TEMPFILE
source $TEMPFILE
rm $TEMPFILE

runsid=$1

eval status=`psql -h $postgres_hostname -U $postgres_user -p $postgres_port -d $postgres_database_configuration -w -c "SELECT status FROM runs WHERE runs_id=$runsid" -A -t`

if [ $status -eq 0 ] ; then # only kick it off if the model is not already running
	echo "runsid=$1" >> data/environment.properties
	eval configuration_id=`psql -h $postgres_hostname -U $postgres_user -p $postgres_port -d $postgres_database_configuration -w -c "SELECT configuration_id FROM runs WHERE runs_id=$runsid" -A -t`
	eval postgres_database_core=`psql -h $postgres_hostname -U $postgres_user -p $postgres_port -d $postgres_database_configuration -w -c "SELECT nameofscenario FROM configuration WHERE configuration_id=$configuration_id" -A -t`
	eval seed=`psql -h $postgres_hostname -U $postgres_user -p $postgres_port -d $postgres_database_configuration -w -c "SELECT seed FROM runs WHERE runs_id=$runsid" -A -t`
	echo "postgres.database.core=$postgres_database_core" >> data/environment.properties
	nohup java -Xdebug -Xrunjdwp:transport=dt_socket,address=8888,server=y,suspend=n -Xmx3072M -jar TransportNSW.jar &> output &
	psql -h $postgres_hostname -U $postgres_user -p $postgres_port -d $postgres_database_configuration -w -c "UPDATE runs SET status=1 WHERE runs_id=$runsid" -A -t > /dev/null
fi
