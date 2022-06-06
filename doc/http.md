To configure confab-videobridge to serve meet:

1. Add prosody certificates to java keystore
2. Configure authbind to allow jvb to use port 443
3. And configure jvb itself in
/etc/confab/videobridge/sip-communicator.properties if installed from package, or in $HOME/.sip-communicator/sip-communicator.properties if running from source.
```
org.confab.videobridge.rest.jetty.host=::
org.confab.videobridge.rest.jetty.port=443
org.confab.videobridge.rest.jetty.ProxyServlet.hostHeader=example.meet.jit.si
org.confab.videobridge.rest.jetty.ProxyServlet.pathSpec=/http-bind
org.confab.videobridge.rest.jetty.ProxyServlet.proxyTo=http://localhost:5280/http-bind
org.confab.videobridge.rest.jetty.ResourceHandler.resourceBase=/usr/share/confab-meet
org.confab.videobridge.rest.jetty.ResourceHandler.alias./config.js=/etc/confab/meet/example.meet.jit.si-config.js
org.confab.videobridge.rest.jetty.ResourceHandler.alias./interface_config.js=/usr/share/confab-meet/interface_config.js
org.confab.videobridge.rest.jetty.RewriteHandler.regex=^/([a-zA-Z0-9]+)$
org.confab.videobridge.rest.jetty.RewriteHandler.replacement=/
org.confab.videobridge.rest.api.jetty.SSIResourceHandler.paths=/
org.confab.videobridge.rest.jetty.tls.port=443
org.confab.videobridge.TCP_HARVESTER_PORT=443
org.confab.videobridge.rest.jetty.sslContextFactory.keyStorePath=/etc/confab/videobridge/example.meet.jit.si.jks
org.confab.videobridge.rest.jetty.sslContextFactory.keyStorePassword=changeit
```
4. You need to start jvb with rest and xmpp interface running. Add the
following to jvb config in etc:
JVB_OPTS="--apis=rest,xmpp"
AUTHBIND=yes
5. If working cross domains, configure CORS
```
org.confab.videobridge.rest.jetty.cors.allowedOrigins=*.meet.jit.si
```
It's also possible to disable the Colibri REST API endpoints with:

```
org.confab.videobridge.ENABLE_REST_COLIBRI=false
```
