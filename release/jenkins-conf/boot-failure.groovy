import jenkins.model.*
def instance = Jenkins.getInstance()
instance.save()
instance.restart()