package uk.gov.gsi.hmrc.rest.dms.utils;

import java.text.MessageFormat;

import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.impl.util.StringUtil;
import uk.gov.gsi.hmrc.rest.dms.utils.Constants;

public class FolderUtils {
	/**
	 * DQL Template get r_object_id of ACL
	 *//*
	protected static final String DQL_GetAcl = "dm_acl WHERE object_name = ''{0}''";

	*//**
	 * DQL Template get r_object_id of Cabinet
	 *//*
	protected static final String DQL_GetCabient = "dm_cabinet WHERE object_name = ''{0}''";

	*//**
	 * DQL Template get r_object_id of Folder filtered by parent folder path
	 *//*
	protected static final String DQL_GetFolder = "dm_folder WHERE FOLDER(''{0}'') AND object_name = ''{1}''";*/


	/**
	 * Create folder path if required.
	 * 
	 * @param s
	 * @param folderPath
	 * @param title
	 *            OPTIONAL title to apply
	 * @param acl
	 *            OPTIONAL acl name to apply
	 * @return
	 * @throws DfException
	 */
	public static IDfId createFolderPath(IDfSession s, String folderPath, String title, String acl, String fType) throws DfException {
		
		final String folders[] = folderPath.split("/");
		String s0 = null, dql = null;
		StringBuilder path = null;
		IDfFolder o = null;
		IDfId id = null;
		IDfACL aclObj = getAcl(s, acl);
		for (final String folderName : folders)
			if (StringUtil.isEmptyOrNull(folderName) == false) {
				if (path == null)
					dql = MessageFormat.format(Constants.DQL_GetCabient, folderName);
				else
					dql = MessageFormat.format(Constants.DQL_GetFolder, path.toString(), folderName);

				id = s.getIdByQualification(dql);
				if (id == null || id.isNull()) {
					if (path != null) {
						if (!StringUtil.isEmptyOrNull(fType)){
							o = (IDfFolder) s.newObject(fType);
							o.link(path.toString());
						} else {
							o = (IDfFolder) s.newObject("dm_folder");
							o.link(path.toString());
						}
					} 
					o.setObjectName(folderName);
					if (StringUtil.isEmptyOrNull(title) == false)
						o.setTitle(title);
					if (aclObj != null)
						o.setACL(aclObj);
					o.save();
					id = o.getObjectId();
					s0 = "Created folder: " + o.getFolderPath(0);
					DfLogger.info(FolderUtils.class, s0, null, null);
				}
				if (path == null)
					path = new StringBuilder(254);
				path.append("/").append(folderName);
			}
		return id;
	}

	/**
	 * Get ACL. First try in system domain. Then install owner. Finally query
	 * for
	 */
	public static IDfACL getAcl(final IDfSession s, final String acl)
			throws DfException {
		IDfACL ret = null;
		String s0 = null;
		if (StringUtil.isEmptyOrNull(acl) == false) {
			s0 = s.getDocbaseOwnerName();
			ret = s.getACL(s0, acl);
			if (ret == null) {
				s0 = s.getServerConfig().getString("r_install_owner");
				ret = s.getACL(s0, acl);
			}
			if (ret == null) {
				s0 = MessageFormat.format(Constants.DQL_GetAcl, acl);
				IDfId id = s.getIdByQualification(s0);
				if (id != null && id.isObjectId())
					ret = (IDfACL) s.getObject(id);
			}
			if (ret == null) {
				s0 = "Failed to get ACL " + acl;
				DfLogger.warn(FolderUtils.class, s0, null, null);
			} else {
				s0 = "Fetched ACL: " + ret.getObjectName();
				s0 += ", in domain: " + ret.getDomain();
				DfLogger.debug(FolderUtils.class, s0, null, null);
			}
		}
		return ret;
	}
}
