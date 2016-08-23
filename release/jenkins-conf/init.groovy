import jenkins.model.*
import java.util.logging.Logger
import hudson.security.*

def logger = Logger.getLogger("")
def installed = false
def initialized = false

def pluginParameter="credentials plain-credentials git gitlab-plugin maven-plugin workflow-aggregator docker-workflow job-dsl"
def plugins = pluginParameter.split()
logger.info("" + plugins)
def instance = Jenkins.getInstance()
def pm = instance.getPluginManager()
def uc = instance.getUpdateCenter()
uc.updateAllSites()

plugins.each {
  logger.info("Checking " + it)
  if (!pm.getPlugin(it)) {
    logger.info("Looking UpdateCenter for " + it)
    if (!initialized) {
      uc.updateAllSites()
      initialized = true
    }
    def plugin = uc.getPlugin(it)
    if (plugin) {
      logger.info("Installing " + it)
    	plugin.deploy()
      installed = true
    }
  }
}

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", "{{ jenkins_admin_password }}")
instance.setSecurityRealm(hudsonRealm)
def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.ADMINISTER, "admin")
instance.setAuthorizationStrategy(strategy)

instance.save()

if(installed) {
  instance.restart()
}