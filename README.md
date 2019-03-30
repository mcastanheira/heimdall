# Heimdall

**Heimdall** is a simple service monitor. It periodically trigger request to the heartbeat endpoints. If the response isn't a HTTP 200 OK, the service is said failed. Each check is registered and you can view the last results from all the services, or the last 10 results from an especific service.

## Installation

Download the latest standalone jar file from <a href="https://github.com/mcastanheira/heimdall/blob/master/jar/" target="_blank">https://github.com/mcastanheira/heimdall/blob/master/jar/</a>.

## Usage

Heimdall needs a configuration file to know some basic information. Create a file like this:

<pre>
{
  :port 3000 ;port where Heimdall will listen for requests
  :check-interval 300 ;interval (in seconds) between service checks 
  :timestamp-mask "dd/MM/yyyy HH:mm:ss" ;mask to be used to format the timestamps of checks}
</pre>

Then you can start Heimdall with the following command:

<pre>java -jar heimdall-&lt;version&gt;-standalone.jar -c &lt;path-to-your-config-file&gt;</pre>

**Note:** if you do not inform the config file path, it's assumed to be **cfg/heimdall.clj**.

On the first run, **Heimdall** will create the database used to store the services and checks in the path **db/heimdall.db**.

After the startup, you can access **Heimdall** in the URL http://&lt;machine-where-heimdall-is-running&gt;:&lt;port-informed-in-the-config-file&gt;

## Options

**-c** or **--config-file** &lt;PATH&gt;

## License

Copyright © 2019

Distributed under the Eclipse Public License either version 1.0 or any later version.
