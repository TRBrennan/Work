/*Amendment script to add to already existing table
Version 1.0 12-12-2016 JGill . As Per EKDMS 1255 */


USE [DM_DMS_docbase]
GO

ALTER TABLE [dbo].[dgms_robotic_config]
ADD robotics_retries [smallint] NOT NULL DEFAULT (3);
GO

ALTER TABLE [dbo].[dgms_robotic_config]
ADD active [tinyint]  NOT NULL DEFAULT (0);
GO

