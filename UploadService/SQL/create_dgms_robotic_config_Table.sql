/*Amendment script to add to already existing table
This should only be run if the table doesnt already exist otherwise use amendment scripts
Modification History
Version 1.0 
Version 2.0 12-12-2016 JGill . As Per EKDMS 1255  Update to include additional column */

Use [DM_DMS_docbase]

DROP TABLE [dbo].[dgms_robotic_config]
GO

CREATE TABLE [dbo].[dgms_robotic_config](
                [form_type] [varchar](60) NOT NULL,
                [form_format][varchar](4) NOT NULL,
                [form_source] [varchar](10) NOT NULL,
                [robotic_category] [varchar](64) NOT NULL,
                [robotic_endpoint] [varchar](512) NOT NULL,
				[message_solution] [varchar](100) NOT NULL,
				[message_workflow_id] [varchar](100) NOT NULL,
				[response_timeout] [varchar] (10) NOT NULL,
				[robotic_retries] [smallint]  NOT NULL DEFAULT (3),
				[active] [tinyint]  NOT NULL DEFAULT (0)
)
